package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import com.don.tryoutisthebest.model.TemporaryFile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TemporaryFileRepository extends ReactiveMongoRepository<TemporaryFile,String> {

    Mono<TemporaryFile> findByStatusAndRequestedToIsContaining(RequestedFileStatus status, String name);

    Mono<TemporaryFile> findByFileName(String fileName);
}
