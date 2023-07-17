package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.resources.UploadRequestDto;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface TemporaryFileService {
    Mono<String> saveTemporaryInfo(FilePart filePart, UploadRequestDto file) throws IOException;

    Mono<TemporaryFile> getAllRequests(String userName);


    void giveApproval(String approvedByWhoUser, String fileName,boolean yesKiNo);
}
