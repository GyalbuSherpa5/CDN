package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.model.FileInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FileInfoRepository extends ReactiveMongoRepository<FileInfo,String> {
}
