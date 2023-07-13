package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.model.FileContent;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;

@JaversSpringDataAuditable
public interface FileContentAuditRepo extends MongoRepository<FileContent, String> {
}
