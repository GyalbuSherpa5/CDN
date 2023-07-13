package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.resources.FileResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface FileInfoService {
    void saveFileInfo(FilePart filePart) throws IOException;
    void updateFileInfo(FilePart filePart, String id) throws IOException;
    Mono<FileResponse> getFileDetail(String id);
    Mono<Void> deleteAllFileInfo();
    Mono<Void> deleteAllFileContent();
    Flux<FileResponse> getAll();
    FileResponse rollbackToSnapshot(String fileContentId, String fileInfoId, int snapshotVersion);

    /*Mono<List<FileResponse>> getFileContentChanges(String id);

    Mono<List<FileResponse>> getFileContentChanges();

    Mono<String> getFileContentAuditStates();

    Mono<String> getFileContentAuditStates(String id);*/
}
