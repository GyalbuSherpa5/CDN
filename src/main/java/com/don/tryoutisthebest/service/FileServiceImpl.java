package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.util.files.FileDetector;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileInfoService fileInfoService;
    private final FileDetector detector;
    private final MinioUtil minioService;

    @Override
    public Mono<String> uploadFile(FilePart filePart) throws TikaException, IOException {
        detector.detect(filePart);
        fileInfoService.saveFileInfo(filePart);
        return Mono.just("done");
    }

    @Override
    public Mono<String> updateFile(FilePart filePart, String id) throws TikaException, IOException {
        detector.detect(filePart);
        fileInfoService.updateFileInfo(filePart, id);

        minioService.putObject(filePart);
        return Mono.just("updated");
    }
}
