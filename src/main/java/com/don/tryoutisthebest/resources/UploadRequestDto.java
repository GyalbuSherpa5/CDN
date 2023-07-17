package com.don.tryoutisthebest.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UploadRequestDto {
    private List<String> requestedTo;
    private String createdBy;
    private int count;
}
