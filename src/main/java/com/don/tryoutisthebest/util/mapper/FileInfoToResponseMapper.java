package com.don.tryoutisthebest.util.mapper;

import com.don.tryoutisthebest.resources.FileResponse;
import com.don.tryoutisthebest.model.FileInfo;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FileInfoToResponseMapper implements Function<FileInfo, FileResponse> {
    @Override
    public FileResponse apply(FileInfo fileInfo) {
        return new FileResponse(
                fileInfo.getId(),
                fileInfo.getFileName(),
                fileInfo.getFilePath(),
                fileInfo.getFileSize(),
                fileInfo.getContentType(),
                fileInfo.getActualData()
        );
    }
}
