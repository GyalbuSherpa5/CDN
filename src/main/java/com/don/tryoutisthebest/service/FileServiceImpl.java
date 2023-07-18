package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.config.MinioConfig;
import com.don.tryoutisthebest.util.files.FileDetector;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileInfoService fileInfoService;
    private final FileDetector detector;
    private final MinioUtil minioService;
    private final MinioConfig minioConfig;

    @Override
    public String uploadFile(FilePart filePart) throws TikaException, IOException {
        detector.detect(filePart);
        minioService.putObject(filePart, minioConfig.getBucketName());
        return fileInfoService.saveFileInfo(filePart);
    }

    @Override
    public Mono<String> updateFile(FilePart filePart) throws TikaException, IOException {
        detector.detect(filePart);
        fileInfoService.updateFileInfo(filePart);

        minioService.putObject(filePart, minioConfig.getBucketName());
        return Mono.just("updated");
    }
}
