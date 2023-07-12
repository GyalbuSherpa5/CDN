package com.don.tryoutisthebest.model;

import com.don.tryoutisthebest.enums.FileInfoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("file_info")
public class FileInfo{
    @Id
    private String id;

    private String name;
    private String path;
    private long size;
    private String contentType;
    private String createdBy;
    private FileInfoStatus status;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date modifiedDate;

    private String fileContentId;

    @Transient
    private FileContent fileContent;
}

