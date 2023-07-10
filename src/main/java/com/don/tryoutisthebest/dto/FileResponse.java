package com.don.tryoutisthebest.dto;

import com.don.tryoutisthebest.model.FileContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {

    private String id;
    private String name;
    private String path;
    private long size;
    private String contentType;
    private String fileContentId;
    private FileContent fileContent;
}
