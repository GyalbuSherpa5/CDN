package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.repository.TemporaryFileRepository;
import com.don.tryoutisthebest.util.files.GetMime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemporaryFileServiceImpl implements TemporaryFileService {

    private final TemporaryFileRepository repository;
    private final GetMime mime;

    @Override
    public void saveTemporaryInfo(FilePart filePart, TemporaryFile file) throws IOException {
        file.setFileName(filePart.filename());
        file.setActualContent(mime.getMime(filePart));
        file.setStatus(RequestedFileStatus.REQUESTED);

        repository.save(file).subscribe();
    }

    @Override
    public Mono<TemporaryFile> getAllRequests(String userName) {
        return repository.findByStatusAndRequestedToIsContaining(RequestedFileStatus.REQUESTED, userName);
    }

    @Override
    public void giveApproval(String approvedByWhoUser, String fileName, boolean yesKiNo) {

        repository.findByFileName(fileName)
                .flatMap(temporaryFile -> {
                    if(yesKiNo){
                        if(temporaryFile.getCount() != 0) {
                            temporaryFile.setApprovedBy(Collections.singletonList(approvedByWhoUser));
                            temporaryFile.setCount(temporaryFile.getCount() - 1);
                        }
                    }
                    else {
                        temporaryFile.setRejectedBy(Collections.singletonList(approvedByWhoUser));
                        temporaryFile.setStatus(RequestedFileStatus.REJECTED);
                    }
                    return repository.save(temporaryFile);
                })
                .flatMap(temporaryFile -> {
                    if(temporaryFile.getCount() ==0 ){
                        temporaryFile.setStatus(RequestedFileStatus.APPROVED);
                        temporaryFile.setActualContent("don");
                    }
                    return repository.save(temporaryFile);
                })
                .subscribe();
    }

}

