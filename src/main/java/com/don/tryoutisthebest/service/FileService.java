package com.don.tryoutisthebest.service;

import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface FileService {
    Mono<String> uploadFile(FilePart filePart) throws TikaException, IOException;

    Mono<String> updateFile(FilePart filePart, String id) throws TikaException, IOException;
}