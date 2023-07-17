package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import com.don.tryoutisthebest.model.TemporaryFile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TemporaryFileRepository extends ReactiveMongoRepository<TemporaryFile, String> {
    Flux<TemporaryFile> findByStatusAndRequestedToIsContaining(RequestedFileStatus status, String name);
    Mono<TemporaryFile> findByFileName(String fileName);
    Flux<TemporaryFile> findByCreatedBy(String userName);
}
