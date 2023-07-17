package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.resources.CheckerResponseDto;
import com.don.tryoutisthebest.resources.MakerResponseDto;
import com.don.tryoutisthebest.resources.UploadRequestDto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface TemporaryFileService {
    Mono<String> saveTemporaryInfo(FilePart filePart, UploadRequestDto file) throws IOException;

    Flux<CheckerResponseDto> getAllRequests(String userName);


    void giveApproval(String approvedByWhoUser, String fileName,boolean yesKiNo);

    Mono<Void> deleteAllFileTemp();

    Flux<MakerResponseDto> getMyRequest(String userName);
}
