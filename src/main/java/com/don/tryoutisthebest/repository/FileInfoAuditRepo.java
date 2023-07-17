package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.model.FileInfo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@JaversSpringDataAuditable
public interface FileInfoAuditRepo extends MongoRepository<FileInfo, String> {
    Optional<FileInfo> findByFileName(String filename);
}
