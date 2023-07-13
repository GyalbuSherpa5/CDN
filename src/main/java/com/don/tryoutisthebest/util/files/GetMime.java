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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class GetMime {
    public String getMime(FilePart filePart) throws IOException {
        File convFile = new File(filePart.filename());
        filePart.transferTo(convFile).subscribe();
        return Files.readString(convFile.toPath());
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
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
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
}
