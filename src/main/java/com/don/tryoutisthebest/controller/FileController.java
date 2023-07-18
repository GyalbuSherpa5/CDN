package com.don.tryoutisthebest.controller;

import com.don.tryoutisthebest.config.MinioConfig;
import com.don.tryoutisthebest.model.FileInfo;
import com.don.tryoutisthebest.resources.FileResponse;
import com.don.tryoutisthebest.service.FileInfoService;
import com.don.tryoutisthebest.service.FileService;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
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
    private final MinioConfig minioConfig;

    @PostMapping("/work/{fileName}")
    public String works(@PathVariable String fileName, @RequestBody Map<String, Object> requestBody) throws IOException {
//        File file = mime.convertStringToFile(requestBody.values().toString(), fileName);
//        minioUtil.putObject(file);

        System.out.println(fileName);
        FilePart filePart = mime.createFilePart(fileName, requestBody.values().toString());
        System.out.println(filePart.filename());
        minioUtil.putObject(filePart,minioConfig.getBucketName());
        return "working";
    }

    @PostMapping("/uploads")
    public String uploadFile(FilePart filePart) throws IOException, TikaException {

        log.info("FileController | uploadFile is called :  ");

        return fileService.uploadFile(filePart);

    }

    @PutMapping("/update")
    public Mono<String> updateFile(FilePart filePart) throws IOException, TikaException {
        log.info("FileController | updateFile is called ");
        return fileService.updateFile(filePart);
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

    @GetMapping("/roll")
    public FileResponse rollbackToSnapshot(@RequestParam() String fileInfoId, @RequestParam("snapshotVersion") int snapshotVersion) {

        return fileInfoService.rollbackToSnapshot(fileInfoId, snapshotVersion);

    }

    @PostMapping("/deleteByFileName/{fileName}")
    public Mono<String> deleteByName(@PathVariable String fileName) {
        return fileInfoService.deleteByFileName(fileName).thenReturn("deleted");
    }

    @GetMapping("/changes")
    public Mono<List<FileInfo>> getFileContentChanges() {

        return fileInfoService.getFileContentChanges();
    }

    @GetMapping("/changes/{id}")
    public Mono<List<FileInfo>> getFileContentChanges(@PathVariable String id) {

        return fileInfoService.getFileContentChanges(id);
    }

    @GetMapping("/fileInfo/states")
    public Mono<Object> getFileContentAuditStates() {

        return fileInfoService.getFileContentAuditStates();
    }

    @GetMapping("/fileInfo/{id}/states")
    public Mono<Object> getFileContentAuditStates(@PathVariable String id) {

        return fileInfoService.getFileContentAuditStates(id);
    }

}
