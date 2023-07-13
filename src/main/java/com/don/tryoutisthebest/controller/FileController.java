package com.don.tryoutisthebest.controller;

import com.don.tryoutisthebest.resources.FileResponse;
import com.don.tryoutisthebest.service.FileInfoService;
import com.don.tryoutisthebest.service.FileService;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("files")
public class FileController {

    private final FileService fileService;
    private final FileInfoService fileInfoService;
    private final GetMime mime;
    private final MinioUtil minioUtil;

    @PostMapping("/work/{fileName}")
    public String works(@PathVariable String fileName, @RequestBody Map<String, Object> requestBody) throws IOException {
//        File file = mime.convertStringToFile(requestBody.values().toString(), fileName);
//        minioUtil.putObject(file);

        System.out.println(fileName);
        FilePart filePart = mime.createFilePart(fileName, requestBody.values().toString());
        System.out.println(filePart.filename());
        minioUtil.putObject(filePart);
        return "working";
    }

    @PostMapping("/uploads")
    public Mono<String> uploadFile(FilePart filePart) throws IOException, TikaException {

        log.info("FileController | uploadFile is called :  ");

        return fileService.uploadFile(filePart);

    }

    @PutMapping("/update/{id}")
    public Mono<String> updateFile(FilePart filePart, @PathVariable String id) throws IOException, TikaException {
        log.info("FileController | updateFile is called ");

        return fileService.updateFile(filePart, id);
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

    @GetMapping("/roll")
    public FileResponse rollbackToSnapshot(@RequestParam() String fileContentId, @RequestParam() String fileInfoId, @RequestParam("snapshotVersion") int snapshotVersion) {

        return fileInfoService.rollbackToSnapshot(fileContentId, fileInfoId, snapshotVersion);

    }

}
