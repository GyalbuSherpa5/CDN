package com.don.tryoutisthebest.resources;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CheckerResponseDto {

    private String fileName;
    private String actualContent;
    private List<String> approvedBy;
    private List<String> rejectedBy;
    private String createdBy;
    private RequestedFileStatus status;

}
