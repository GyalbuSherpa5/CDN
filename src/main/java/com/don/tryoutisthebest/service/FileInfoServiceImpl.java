package com.don.tryoutisthebest.service;

import com.don.tryoutisthebest.enums.FileInfoStatus;
import com.don.tryoutisthebest.model.FileContent;
import com.don.tryoutisthebest.model.FileInfo;
import com.don.tryoutisthebest.repository.FileContentAuditRepo;
import com.don.tryoutisthebest.repository.FileContentRepository;
import com.don.tryoutisthebest.repository.FileInfoAuditRepo;
import com.don.tryoutisthebest.repository.FileInfoRepository;
import com.don.tryoutisthebest.resources.FileResponse;
import com.don.tryoutisthebest.util.files.GetMime;
import com.don.tryoutisthebest.util.mapper.FileInfoToResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    private final GetMime mime;
    private final Javers javers;
    private final FileInfoRepository fileInfoRepository;
    private final FileContentRepository fileContentRepository;

    private final FileInfoAuditRepo fileInfoAuditRepo;
    private final FileContentAuditRepo fileContentAuditRepo;
    private final FileInfoToResponseMapper fileInfoToResponseMapper;

   /*
        # Heartfelt condolence to reactive save.

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
    }*/

    @Override
    @Transactional
    public String saveFileInfo(FilePart filePart) throws IOException {

        FileContent fileContent = new FileContent();
        fileContent.setActualData(mime.getMime(filePart));
        fileContent.setFileName(filePart.filename());
        fileContent.setFileStatus(FileInfoStatus.ACTIVE);
        FileContent savedContent = fileContentAuditRepo.save(fileContent);

        String id = savedContent.getId();

        FileInfo fileInfo = getFileInfo(filePart);
        fileInfo.setFileContentId(id);

        fileInfoAuditRepo.save(fileInfo);
        return "uploaded";
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

    /*
        Rest in peace
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
    }*/

    public void updateFileInfo(FilePart filePart, String id) throws IOException {

        String actualContent = mime.getMime(filePart);

        FileContent fileContent = fileContentAuditRepo.findById(id).orElseThrow(() -> new RuntimeException("not found"));
        fileContent.setActualData(actualContent);

        fileContentAuditRepo.save(fileContent);

        FileInfo byFileContentId = fileInfoAuditRepo.findByFileContentId(id);
        byFileContentId.setPath("dummy2");
        byFileContentId.setSize(filePart.headers().size());
        byFileContentId.setCreatedBy("don2");
        fileInfoAuditRepo.save(byFileContentId);

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

    /*

    we'll see you later hai bro

    @Override
    public FileResponse rollbackToSnapshot(String fileContentId, String fileInfoId, int snapshotVersion) {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(fileContentId, FileContent.class).withVersion(snapshotVersion);
        AtomicReference<FileContent> fileContent = new AtomicReference<>(new FileContent());
        javers.findSnapshots(jqlQuery.build()).forEach(snapshot -> {
            CdoSnapshotState cdoSnapshotState = snapshot.getState();
            String json = javers.getJsonConverter().toJson(cdoSnapshotState);

            fileContent.set(javers.getJsonConverter().fromJson(json, FileContent.class));

        });
        fileContentAuditRepo.save(fileContent.get());

        QueryBuilder jqlQuery2 = QueryBuilder.byInstanceId(fileInfoId, FileInfo.class).withVersion(snapshotVersion);
        AtomicReference<FileInfo> fileInfo = new AtomicReference<>(new FileInfo());

        javers.findSnapshots(jqlQuery2.build()).forEach(snapshot -> {
            CdoSnapshotState cdoSnapshotState = snapshot.getState();
            String json = javers.getJsonConverter().toJson(cdoSnapshotState);

            fileInfo.set(javers.getJsonConverter().fromJson(json, FileInfo.class));

        });

        fileInfoAuditRepo.save(fileInfo.get());

        return fileInfoAuditRepo.findById(fileInfoId).map(fileInfoToResponseMapper).orElseThrow(() -> new RuntimeException("id not found"));

    }
*/

    @Override
    public FileResponse rollbackToSnapshot(String fileContentId, String fileInfoId, int snapshotVersion) {
        FileContent fileContent = rollbackFileContentToSnapshot(fileContentId, snapshotVersion);
        FileInfo fileInfo = rollbackFileInfoToSnapshot(fileInfoId, snapshotVersion);

        fileContentAuditRepo.save(fileContent);
        fileInfoAuditRepo.save(fileInfo);

        return fileInfoAuditRepo.findById(fileInfoId)
                .map(fileInfoToResponseMapper)
                .orElseThrow(() -> new RuntimeException("id not found"));
    }

    @Override
    public Mono<Void> deleteByFileName(String fileName) {
        // Assume fileInfoRepository.findByName returns a Mono<FileInfo>
        return fileInfoRepository.findByName(fileName)
                .flatMap(fileInfo -> {
                    fileInfo.setStatus(FileInfoStatus.DELETED);
                    // Assume fileInfoRepository.save returns a Mono<FileInfo>
                    return fileInfoRepository.save(fileInfo);
                })
                .then();
    }

    private FileContent rollbackFileContentToSnapshot(String fileContentId, int snapshotVersion) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(fileContentId, FileContent.class).withVersion(snapshotVersion);
        Optional<CdoSnapshot> snapshot = javers.findSnapshots(jqlQuery.build()).stream().findFirst();

        return snapshot.map(s -> javers.getJsonConverter().fromJson(javers.getJsonConverter().toJson(s.getState()), FileContent.class))
                .orElse(new FileContent());
    }

    private FileInfo rollbackFileInfoToSnapshot(String fileInfoId, int snapshotVersion) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(fileInfoId, FileInfo.class).withVersion(snapshotVersion);
        Optional<CdoSnapshot> snapshot = javers.findSnapshots(jqlQuery.build()).stream().findFirst();

        return snapshot.map(s -> javers.getJsonConverter().fromJson(javers.getJsonConverter().toJson(s.getState()), FileInfo.class))
                .orElse(new FileInfo());
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
