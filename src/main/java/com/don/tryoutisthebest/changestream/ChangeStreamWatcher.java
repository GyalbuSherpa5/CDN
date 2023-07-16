package com.don.tryoutisthebest.changestream;


import com.don.tryoutisthebest.model.FileContent;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeStreamWatcher {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MinioUtil minioService;
    private final GetMime mime;

    public void watchDatabaseChanges() {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("operationType").is("update"))
        );

        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(aggregation)
                .build();

        reactiveMongoTemplate.changeStream("file_content", options, FileContent.class)
                .doOnNext(changeEvent -> {
                    try {
                        processChange(Objects.requireNonNull(changeEvent.getBody()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribe();
    }

    private void processChange(FileContent changedDocument) throws IOException {
        log.info("Upload file after change in db ");

        FilePart part = mime.createFilePart(changedDocument.getFileName(), changedDocument.getActualData());
        minioService.putObject(part);
    }

}