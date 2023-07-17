package com.don.tryoutisthebest.util.files;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class GetMime {

    public String getMime(FilePart filePart) {
        AtomicReference<String> actualContent = new AtomicReference<>("");

        filePart.content().map(dataBuffer -> {
            byte[] contentBytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(contentBytes);
            actualContent.set(new String(contentBytes, StandardCharsets.UTF_8));
            return new String(contentBytes, StandardCharsets.UTF_8);
        }).subscribe();

        return actualContent.get();
    }

    public File convertStringToFile(String content, String filename) throws IOException {
        File file = new File(filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    public FilePart createFilePart(String filename, String fileData) {
        return new FilePart() {
            @NotNull
            @Override
            public String filename() {
                return filename;
            }

            @NotNull
            @Override
            public Mono<Void> transferTo(@NotNull Path dest) {
                return Mono.fromCallable(() -> {
                    Files.writeString(dest, fileData);
                    return null; // Returning null since the write operation doesn't produce a useful result
                }).subscribeOn(Schedulers.boundedElastic()).then(); // Executing the operation on a separate thread
            }

            @NotNull
            @Override
            public String name() {
                return "file";
            }

            @NotNull
            @Override
            public HttpHeaders headers() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(getContentTypeFromFileName(filename));
                headers.setContentDispositionFormData("file", filename);
                return headers;
            }

            @NotNull
            @Override
            public Flux<DataBuffer> content() {
                byte[] fileBytes = fileData.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = new DefaultDataBufferFactory().wrap(fileBytes);
                return Flux.just(buffer);
            }
        };
    }

    public MediaType getContentTypeFromFileName(String filename) {
        Path path = Paths.get(filename);
        String contentTypeString = URLConnection.guessContentTypeFromName(path.toString());
        if (contentTypeString == null) {
            contentTypeString = "application/octet-stream"; // Default to octet-stream if content type cannot be determined
        }
        return MediaType.parseMediaType(contentTypeString);
    }
}
