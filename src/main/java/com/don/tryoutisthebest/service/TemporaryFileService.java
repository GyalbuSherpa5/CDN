package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.model.TemporaryFile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface TemporaryFileService {
    void saveTemporaryInfo(FilePart filePart, TemporaryFile file) throws IOException;

    Mono<TemporaryFile> getAllRequests(String userName);


    void giveApproval(String approvedByWhoUser, String fileName,boolean yesKiNo);
}
