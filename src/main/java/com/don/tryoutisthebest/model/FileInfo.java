package com.don.tryoutisthebest.model;

import com.don.tryoutisthebest.enums.FileInfoStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("file_info")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FileInfo {
    @Id
    private String id;

    private String fileName;
    private String filePath;
    private long fileSize;
    private String contentType;
    private String createdBy;
    private FileInfoStatus fileStatus;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

    private String actualData;
}

