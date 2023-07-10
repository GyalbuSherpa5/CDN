package com.don.tryoutisthebest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class TryoutisthebestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TryoutisthebestApplication.class, args);
    }

}
