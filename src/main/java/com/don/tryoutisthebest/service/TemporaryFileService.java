package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.resources.CheckerResponseDto;
import com.don.tryoutisthebest.resources.MakerResponseDto;
import com.don.tryoutisthebest.resources.UploadRequestDto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface TemporaryFileService {
    /**
     * This method saves data to the temporary collection for verification
     * @param filePart This is reactive file
     * @param file
     * @return whether file is saved or not
     */
    Mono<String> saveTemporaryInfo(FilePart filePart, UploadRequestDto file) throws IOException;

    Flux<CheckerResponseDto> getAllRequests(String userName);


    void giveApproval(String approvedByWhoUser, String fileName);

    void reject(String approvedByWhoUser, String fileName);

    Mono<Void> deleteAllFileTemp();

    Flux<MakerResponseDto> getMyRequest(String userName);
}
