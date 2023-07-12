package com.don.tryoutisthebest.util.minio;

import com.don.tryoutisthebest.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @SneakyThrows
    public void putObject(File file) {

        log.info("MinioUtil | putObject is called");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("username", "don");

        InputStream targetStream = FileUtils.openInputStream(file);

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(file.getPath());

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(file.getPath())
                        .stream(targetStream, -1, minioConfig.getFileSize())
                        .contentType(contentType)
                        .userMetadata(metadata)
                        .build()
        );
    }

    @SneakyThrows
    public void putObject(FilePart filePart) {

        log.info("MinioUtil | putObject is called");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("username", "don");

        Flux<DataBuffer> content = filePart.content();
        InputStream inputStream = getInputStreamFromFluxDataBuffer(content);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .object(filePart.filename())
                        .stream(inputStream, -1, minioConfig.getFileSize())
                        .contentType(String.valueOf(filePart.headers().getContentType()))
                        .userMetadata(metadata)
                        .build()
        );
    }

    public InputStream getInputStreamFromFluxDataBuffer(Flux<DataBuffer> data) throws IOException {
        PipedOutputStream osPipe = new PipedOutputStream();
        PipedInputStream isPipe = new PipedInputStream(osPipe);

        DataBufferUtils.write(data, osPipe)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    try {
                        osPipe.close();
                    } catch (IOException ignored) {
                    }
                })
                .subscribe(DataBufferUtils.releaseConsumer());
        return isPipe;
    }


    @SneakyThrows
    public boolean bucketExists(String bucketName) {

        log.info("MinioUtil | bucketExists is called");

        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        if (found) {
            log.info("MinioUtil | bucketExists | message : {} exists", bucketName);
        } else {
            log.info("MinioUtil | bucketExists | message : {} does not exist", bucketName);
        }

        return found;
    }

    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        log.info("MinioUtil | makeBucket is called");

        boolean flag = bucketExists(bucketName);

        log.info("MinioUtil | makeBucket | flag : {}", flag);

        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            return true;
        } else {
            return false;
        }
    }

    // List all objects from the specified bucket
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {

        log.info("MinioUtil | listObjects is called");

        boolean flag = bucketExists(bucketName);

        log.info("MinioUtil | listObjects | flag : {} ", flag);

        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).includeUserMetadata(true).build());
        }
        return null;
    }

    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {

        log.info("MinioUtil | listObjectNames is called");

        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);

        log.info("MinioUtil | listObjectNames | flag : {} ", flag);

        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        } else {
            listObjectNames.add(" Bucket does not exist ");
        }

        log.info("MinioUtil | listObjectNames | listObjectNames size : {} ", listObjectNames.size());

        return listObjectNames;
    }

    // Get metadata of the object from the specified bucket
    @SneakyThrows
    public StatObjectResponse statObject(String bucketName, String objectName) {
        log.info("MinioUtil | statObject is called");

        boolean flag = bucketExists(bucketName);
        log.info("MinioUtil | statObject | flag : {} ", flag);
        if (flag) {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectName).build());

            log.info("MinioUtil | statObject | stat : {} ", stat);

            return stat;
        }
        return null;
    }

    // Get a file object as a stream from the specified bucket
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        log.info("MinioUtil | getObject is called");

        boolean flag = bucketExists(bucketName);
        log.info("MinioUtil | getObject | flag : {} ", flag);

        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build());

                log.info("MinioUtil | getObject | stream : {} ", stream);
                return stream;
            }
        }
        return null;
    }
}



