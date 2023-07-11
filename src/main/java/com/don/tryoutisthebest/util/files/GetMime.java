package com.don.tryoutisthebest.util.files;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class GetMime {
    public String getMime(FilePart filePart) throws IOException {
        File convFile = new File(filePart.filename());
        filePart.transferTo(convFile).subscribe();
        return Files.readString(convFile.toPath());
    }

    public File convertStringToFile(String content, String filename) throws IOException {
        File file = new File(filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    /*public FilePart convertFileToFilePart(File file) {
        FileSystemResource fileResource = new FileSystemResource(file);
        return new FilePart("file", fileResource);
    }*/

}
