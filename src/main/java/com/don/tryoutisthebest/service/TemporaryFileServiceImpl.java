package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import com.don.tryoutisthebest.exception.FileProcessingException;
import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.repository.TemporaryFileRepository;
import com.don.tryoutisthebest.resources.CheckerResponseDto;
import com.don.tryoutisthebest.resources.MakerResponseDto;
import com.don.tryoutisthebest.resources.UploadRequestDto;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.mapper.CheckerResponseMapper;
import com.don.tryoutisthebest.util.mapper.MakerResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class TemporaryFileServiceImpl implements TemporaryFileService {

    private final TemporaryFileRepository repository;
    private final GetMime mime;
    private final CheckerResponseMapper checkerResponseMapper;
    private final MakerResponseMapper makerResponseMapper;

    @Override
    public Mono<String> saveTemporaryInfo(FilePart filePart, UploadRequestDto file) throws IOException {
        TemporaryFile files = new TemporaryFile();
        files.setFileName(filePart.filename());
        files.setActualContent(mime.getMime(filePart));
        files.setRequestedTo(file.getRequestedTo());
        files.setCreatedBy(file.getCreatedBy());
        files.setStatus(RequestedFileStatus.REQUESTED);
        files.setCount(file.getCount());

        return repository.findByFileName(filePart.filename())
                .flatMap(temporaryFile -> Mono.error(new FileProcessingException("File Name already exist")))
                .switchIfEmpty(Mono.defer(() ->
                          repository.save(files)
                )).thenReturn("Saved successfully");
    }

    @Override
    public Flux<CheckerResponseDto> getAllRequests(String userName) {
        return repository.
                findByStatusAndRequestedToIsContaining(RequestedFileStatus.REQUESTED, userName)
                .map(checkerResponseMapper);
    }

    @Override
    public void giveApproval(String approvedByWhoUser, String fileName, boolean yesKiNo) {

        repository.findByFileName(fileName)
                .flatMap(temporaryFile -> {
                    if (yesKiNo) {
                        if (temporaryFile.getCount() != 0) {
                            temporaryFile.setApprovedBy(Collections.singletonList(approvedByWhoUser));
                            temporaryFile.setCount(temporaryFile.getCount() - 1);
                        }
                    } else {
                        temporaryFile.setRejectedBy(Collections.singletonList(approvedByWhoUser));
                        temporaryFile.setStatus(RequestedFileStatus.REJECTED);
                    }
                    return repository.save(temporaryFile);
                })
                .flatMap(temporaryFile -> {
                    if (temporaryFile.getCount() == 0) {
                        temporaryFile.setStatus(RequestedFileStatus.APPROVED);
                        temporaryFile.setActualContent("don");
                    }
                    return repository.save(temporaryFile);
                })
                .subscribe();
    }

    @Override
    public Mono<Void> deleteAllFileTemp() {
        return repository.deleteAll();
    }

    @Override
    public Flux<MakerResponseDto> getMyRequest(String userName) {
        return repository.findByCreatedBy(userName)
                .map(makerResponseMapper);
    }

}

