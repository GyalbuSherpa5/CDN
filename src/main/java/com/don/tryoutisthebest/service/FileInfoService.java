package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.dto.FileResponse;
import com.don.tryoutisthebest.model.FileContent;
import com.don.tryoutisthebest.model.FileInfo;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.xml.sax.SAXException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface FileInfoService {
    void saveFileInfo(FilePart filePart) throws IOException;
    void updateFileInfo(FilePart filePart, String id) throws IOException;
    Mono<FileResponse> getFileDetail(String id);
    Mono<Void> deleteAllFileInfo();
    Mono<Void> deleteAllFileContent();
    Flux<FileResponse> getAll();
}
