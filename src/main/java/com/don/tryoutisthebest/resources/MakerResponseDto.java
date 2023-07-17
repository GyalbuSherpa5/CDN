package com.don.tryoutisthebest.resources;

import com.don.tryoutisthebest.enums.RequestedFileStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MakerResponseDto {
    private String fileName;
    private List<String> requestedTo;
    private List<String> approvedBy;
    private List<String> rejectedBy;
    private RequestedFileStatus status;
}
