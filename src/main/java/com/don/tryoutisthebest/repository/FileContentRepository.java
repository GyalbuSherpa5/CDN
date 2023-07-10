package com.don.tryoutisthebest.repository;

import com.don.tryoutisthebest.model.FileContent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FileContentRepository extends ReactiveMongoRepository<FileContent,String> {

}
