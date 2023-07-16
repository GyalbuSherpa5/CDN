package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.model.FileInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FileInfoRepository extends ReactiveMongoRepository<FileInfo,String> {

    Mono<FileInfo> findByName(String fileName);
}
