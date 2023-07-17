package com.don.tryoutisthebest.util.mapper;

import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.resources.CheckerResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CheckerResponseMapper implements Function<TemporaryFile, CheckerResponseDto> {
    @Override
    public CheckerResponseDto apply(TemporaryFile temporaryFile) {
        return new CheckerResponseDto(
                temporaryFile.getFileName(),
                temporaryFile.getActualContent(),
                temporaryFile.getApprovedBy(),
                temporaryFile.getRejectedBy(),
                temporaryFile.getCreatedBy(),
                temporaryFile.getStatus()
        );
    }
}
