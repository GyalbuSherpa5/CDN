package com.don.tryoutisthebest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("actual_data")
    private String actualData;
}
