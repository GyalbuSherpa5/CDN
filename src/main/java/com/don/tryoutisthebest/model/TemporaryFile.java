package com.don.tryoutisthebest.model;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("temporary_file")
public class TemporaryFile {
    @Id
    private String id;

    private String fileName;
    private String actualContent;
    private String contentType;
    private List<String> requestedTo;
    private List<String> approvedBy = new ArrayList<>();
    private List<String> rejectedBy = new ArrayList<>();
    private String createdBy;
    private RequestedFileStatus status;
    private int count;
    private int approvedCount;

    @CreatedDate
    private Date createdDate;

}
