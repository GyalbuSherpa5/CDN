package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.config.MinioConfig;
import com.don.tryoutisthebest.enums.RequestedFileStatus;
import com.don.tryoutisthebest.exception.ApprovalException;
import com.don.tryoutisthebest.exception.FileProcessingException;
import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.repository.TemporaryFileRepository;
import com.don.tryoutisthebest.resources.CheckerResponseDto;
import com.don.tryoutisthebest.resources.MakerResponseDto;
import com.don.tryoutisthebest.resources.UploadRequestDto;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.mapper.CheckerResponseMapper;
import com.don.tryoutisthebest.util.mapper.MakerResponseMapper;
import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class TemporaryFileServiceImpl implements TemporaryFileService {

    private final TemporaryFileRepository repository;
    private final GetMime mime;
    private final CheckerResponseMapper checkerResponseMapper;
    private final MakerResponseMapper makerResponseMapper;
    private final FileService fileService;
    private final MinioUtil minioUtil;
    private final MinioConfig minioConfig;

    @Override
    public Mono<String> saveTemporaryInfo(FilePart filePart, UploadRequestDto file) {
        TemporaryFile files = new TemporaryFile();
        files.setFileName(filePart.filename());

        if (String.valueOf(filePart.headers().getContentType()).startsWith("image/")) {
            minioUtil.putObject(filePart, minioConfig.getTempBucket());
        } else {
            files.setActualContent(mime.getMime(filePart));
        }
        files.setContentType(String.valueOf(filePart.headers().getContentType()));
        files.setRequestedTo(file.getRequestedTo());
        files.setCreatedBy(file.getCreatedBy());
        files.setStatus(RequestedFileStatus.REQUESTED);
        files.setCount(file.getCount());

        return repository.findByFileName(filePart.filename())
                .flatMap(temporaryFile -> Mono.error(new FileProcessingException("File Name already exist")))
                .switchIfEmpty(Mono.defer(() ->
                        repository.save(files)
                )).thenReturn("Saved successfully");
    }

    @Override
    public Flux<CheckerResponseDto> getAllRequests(String userName) {
        return repository.
                findByStatusAndRequestedToIsContaining(RequestedFileStatus.REQUESTED, userName)
                .map(checkerResponseMapper);
    }

    /*@Override
    public void giveApproval(String approvedByWhoUser, String fileName, boolean yesKiNo) {
        repository.findByFileName(fileName)
                .flatMap(temporaryFile -> {
                    temporaryFile.setCount(temporaryFile.getRequestedTo().size());
                    if (yesKiNo && temporaryFile.getCount() != 0) {
                        // Check if the approvedByWhoUser is not already present in the approvedBy list
                        if (!temporaryFile.getApprovedBy().contains(approvedByWhoUser)) {
                            temporaryFile.getApprovedBy().add(approvedByWhoUser);
                        }
                        temporaryFile.setApprovedCount(temporaryFile.getApprovedCount() + 1);
                    } else {
                        if (!temporaryFile.getRejectedBy().contains(approvedByWhoUser)) {
                            temporaryFile.getRejectedBy().add(approvedByWhoUser);
                        }
                        temporaryFile.setStatus(RequestedFileStatus.REJECTED);
                    }
                    return repository.save(temporaryFile);
                })
                .flatMap(temporaryFile -> {
                    if (temporaryFile.getCount() == temporaryFile.getApprovedCount()) {
                        temporaryFile.setStatus(RequestedFileStatus.APPROVED);

                        if (temporaryFile.getContentType().startsWith("image/")) {
                                minioUtil.migrateObject(minioConfig.getBucketName(), minioConfig.getTempBucket(), temporaryFile.getFileName());
                        }
                        else{
                            FilePart filePart = mime.createFilePart(fileName, temporaryFile.getActualContent());
                            try {
                                fileService.uploadFile(filePart);
                            } catch (TikaException | IOException e) {
                                return Mono.error(new FileProcessingException("gayena.."));
                            }
                        }

                    }
                    return repository.save(temporaryFile);
                })
                .subscribe();
    }*/

    @Override
    public void giveApproval(String approvedByWhoUser, String fileName) {
        repository.findByFileName(fileName)
                .flatMap(temporaryFile -> approve(temporaryFile, approvedByWhoUser))
                .subscribe();
    }

    public Mono<TemporaryFile> approve(TemporaryFile temporaryFile, String approvedByWhoUser) {
        temporaryFile.setCount(temporaryFile.getRequestedTo().size());
        processApproval(temporaryFile, approvedByWhoUser);

        return repository.save(temporaryFile).
                flatMap(this::handleFinalStatus);
    }

    public void processApproval(TemporaryFile temporaryFile, String approvedByWhoUser) {
        if (!temporaryFile.getApprovedBy().contains(approvedByWhoUser)) {
            temporaryFile.getApprovedBy().add(approvedByWhoUser);
            temporaryFile.setApprovedCount(temporaryFile.getApprovedCount() + 1);
        }
        else {
            log.error("TemporaryFileServiceImpl | Error ! already approved ");
            throw new ApprovalException("already approved!!");
        }

    }

    @Override
    public void reject(String approvedByWhoUser, String fileName) {
        repository.findByFileName(fileName)
                .flatMap(temporaryFile -> rejects(temporaryFile, approvedByWhoUser))
                .subscribe();
    }

    public Mono<TemporaryFile> rejects(TemporaryFile temporaryFile, String approvedByWhoUser) {

        processRejection(temporaryFile, approvedByWhoUser);

        return repository.save(temporaryFile).
                flatMap(this::handleFinalStatus);
    }

    public void processRejection(TemporaryFile temporaryFile, String approvedByWhoUser) {
        if (!temporaryFile.getRejectedBy().contains(approvedByWhoUser)) {
            temporaryFile.getRejectedBy().add(approvedByWhoUser);
            temporaryFile.setStatus(RequestedFileStatus.REJECTED);
        }
        else {
            log.error("TemporaryFileServiceImpl | Error ! already rejected");
            throw new ApprovalException("already rejected!!");
        }

    }

    public Mono<TemporaryFile> handleFinalStatus(TemporaryFile temporaryFile) {
        if (temporaryFile.getCount() == temporaryFile.getApprovedCount()) {
            temporaryFile.setStatus(RequestedFileStatus.APPROVED);
            if (temporaryFile.getContentType().startsWith("image/")) {
                minioUtil.migrateObject(minioConfig.getBucketName(), minioConfig.getTempBucket(), temporaryFile.getFileName());
            } else {
                FilePart filePart = mime.createFilePart(temporaryFile.getFileName(), temporaryFile.getActualContent());
                try {
                    fileService.uploadFile(filePart);
                } catch (TikaException | IOException e) {
                    return Mono.error(new FileProcessingException("gayena.."));
                }
            }
        }
        return repository.save(temporaryFile);
    }


    @Override
    public Mono<Void> deleteAllFileTemp() {
        return repository.deleteAll();
    }

    @Override
    public Flux<MakerResponseDto> getMyRequest(String userName) {
        return repository.findByCreatedBy(userName)
                .map(makerResponseMapper);
    }

}

