package com.don.tryoutisthebest.util.mapper;

import com.don.tryoutisthebest.model.TemporaryFile;
import com.don.tryoutisthebest.resources.MakerResponseDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MakerResponseMapper implements Function<TemporaryFile, MakerResponseDto> {
    @Override
    public MakerResponseDto apply(TemporaryFile temporaryFile) {
        return new MakerResponseDto(
                temporaryFile.getFileName(),
                temporaryFile.getRequestedTo(),
                temporaryFile.getApprovedBy(),
                temporaryFile.getRejectedBy(),
                temporaryFile.getStatus()
        );
    }
}
