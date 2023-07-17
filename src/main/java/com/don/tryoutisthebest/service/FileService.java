package com.don.tryoutisthebest.service;

import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface FileService {
    String uploadFile(FilePart filePart) throws TikaException, IOException;

    Mono<String> updateFile(FilePart filePart) throws TikaException, IOException;
}
