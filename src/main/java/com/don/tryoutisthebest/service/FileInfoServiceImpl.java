package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.resources.FileResponse;
import com.don.tryoutisthebest.enums.FileInfoStatus;
import com.don.tryoutisthebest.model.FileContent;
import com.don.tryoutisthebest.model.FileInfo;
import com.don.tryoutisthebest.repository.FileContentRepository;
import com.don.tryoutisthebest.repository.FileInfoRepository;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.mapper.FileInfoToResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    private final GetMime mime;
    private final FileInfoRepository fileInfoRepository;
    private final FileContentRepository fileContentRepository;
    private final FileInfoToResponseMapper fileInfoToResponseMapper;

    @Override
    public void saveFileInfo(FilePart filePart) throws IOException{

        log.info("FileInfoServiceImpl | saving fileContent ");
        FileContent fileContent = new FileContent();
        fileContent.setActualData(mime.getMime(filePart));
        fileContent.setFileName(filePart.filename());
        Mono<FileContent> savedFileContentMono = fileContentRepository.save(fileContent);

        FileInfo fileInfo = getFileInfo(filePart);

        log.info("FileInfoServiceImpl | saving fileInfo ");
        savedFileContentMono
                .flatMap(savedFileContent -> {
                    fileInfo.setFileContentId(savedFileContent.getId());
                    return fileInfoRepository.save(fileInfo);
                }).subscribe();
    }

    @NotNull
    private static FileInfo getFileInfo(FilePart filePart) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(filePart.filename());
        fileInfo.setPath("dummy");
        fileInfo.setSize(filePart.headers().size());
        fileInfo.setCreatedBy("don");
        fileInfo.setStatus(FileInfoStatus.ACTIVE);
        fileInfo.setContentType(String.valueOf(filePart.headers().getContentType()));
        return fileInfo;
    }

    @Override
    public void updateFileInfo(FilePart filePart, String id) throws IOException{

        String actualContent = mime.getMime(filePart);

        log.info("FileInfoServiceImpl | updating fileInfo ");
        fileInfoRepository.findById(id)
                .flatMap(fileInfo -> {
                    fileInfo.setPath("path");
                    fileInfo.setName("hero");
                    return fileInfoRepository.save(fileInfo);
                })
                .map(FileInfo::getFileContentId)
                .flatMap(contentId -> fileContentRepository.findById(contentId)
                        .flatMap(fileContent -> {
                            fileContent.setActualData(actualContent);
                            return fileContentRepository.save(fileContent);
                        })).subscribe();
    }

    @Override
    public Mono<FileResponse> getFileDetail(String id) {
        log.info("FileInfoServiceImpl | inside getFileDetail ");
        return fileInfoRepository.findById(id)
                .flatMap(fileInfo -> {
                    Mono<FileContent> fileContentMono = fileContentRepository
                            .findById(fileInfo.getFileContentId());
                    return fileContentMono.map(fileContent -> {
                        fileInfo.setFileContent(fileContent);
                        return fileInfo;
                    });
                })
                .map(fileInfoToResponseMapper);
    }

    @Override
    public Flux<FileResponse> getAll() {
        log.info("FileInfoServiceImpl | inside getAll ");
        return fileInfoRepository.findAll()
                .flatMap(fileInfo -> {
                    Mono<FileContent> fileContentMono = fileContentRepository
                            .findById(fileInfo.getFileContentId());
                    return fileContentMono.map(fileContent -> {
                        fileInfo.setFileContent(fileContent);
                        return fileInfo;
                    });
                })
                .map(fileInfoToResponseMapper);
    }

    @Override
    public Mono<Void> deleteAllFileInfo() {
        log.info("FileInfoServiceImpl | inside deleteAllFileInfo ");
        return fileInfoRepository.deleteAll();
    }

    @Override
    public Mono<Void> deleteAllFileContent() {
        log.info("FileInfoServiceImpl | inside deleteAllFileContent ");
        return fileContentRepository.deleteAll();
    }


}
