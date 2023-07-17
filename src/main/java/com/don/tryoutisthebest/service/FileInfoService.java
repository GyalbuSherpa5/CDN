package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.model.FileInfo;
import com.don.tryoutisthebest.resources.FileResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface FileInfoService {
    String saveFileInfo(FilePart filePart) throws IOException;

    void updateFileInfo(FilePart filePart) throws IOException;

    Mono<FileResponse> getFileDetail(String id);

    Mono<Void> deleteAllFileInfo();

    Flux<FileResponse> getAll();

    FileResponse rollbackToSnapshot(String fileInfoId, int snapshotVersion);

    Mono<Void> deleteByFileName(String fileName);

    Mono<List<FileInfo>> getFileContentChanges(String id);

    Mono<List<FileInfo>> getFileContentChanges();

    Mono<Object> getFileContentAuditStates();

    Mono<Object> getFileContentAuditStates(String id);


}
