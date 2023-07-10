package com.don.tryoutisthebest.controller;

import com.don.tryoutisthebest.dto.FileResponse;
import com.don.tryoutisthebest.service.FileInfoService;
import com.don.tryoutisthebest.util.files.DetectActualContent;
import com.don.tryoutisthebest.util.files.FileDetector;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("files")
public class FileController {

    private final FileInfoService fileInfoService;
    private final FileDetector detector;
    private final MinioUtil minioService;

    @PostMapping("/work/{id}/{data}")
    public String works(@PathVariable String id, @PathVariable String data) {
        System.out.println(id);
        System.out.println(data);
        return "working";
    }

    @PostMapping("/uploads")
    public Mono<String> uploadFile(FilePart filePart) throws IOException, TikaException {
        log.info("FileController | uploadFile is called ");
        detector.detect(filePart);
        minioService.putObject(filePart);
        fileInfoService.saveFileInfo(filePart);

        return Mono.just("saved");

    }

    @PutMapping("/update/{id}")
    public Mono<String> updateFile(FilePart filePart, @PathVariable String id) throws IOException {
        log.info("FileController | updateFile is called ");
        fileInfoService.updateFileInfo(filePart, id);

//        minioService.putObject(filePart);

        return Mono.just("updated");
    }

    @GetMapping("/getById/{id}")
    public Mono<FileResponse> getFileDetail(@PathVariable String id) {
        return fileInfoService.getFileDetail(id);
    }

    @GetMapping("/getAll")
    public Flux<FileResponse> getAll() {
        return fileInfoService.getAll();
    }

    @DeleteMapping("/deleteFileInfo")
    public Mono<Void> deleteAll() {
        return fileInfoService.deleteAllFileInfo();
    }

    @DeleteMapping("/deleteFileContent")
    public Mono<Void> deleteAllContent() {
        return fileInfoService.deleteAllFileContent();
    }

}
