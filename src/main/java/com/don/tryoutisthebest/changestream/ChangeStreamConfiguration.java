package com.don.tryoutisthebest.changestream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChangeStreamConfiguration {

    private final ChangeStreamWatcher changeStreamWatcher;

    public ChangeStreamConfiguration(ChangeStreamWatcher changeStreamWatcher) {
        this.changeStreamWatcher = changeStreamWatcher;
    }

    @Bean
    public void startChangeStream() {
        changeStreamWatcher.watchDatabaseChanges();
    }
}