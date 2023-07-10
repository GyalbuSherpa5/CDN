package com.don.tryoutisthebest.util.files;

import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FileDetector {

    private final DetectActualContent detectActualExtension;
    public void detect(FilePart filePart) throws TikaException, IOException {
        String fileExtension = String.valueOf(filePart.headers().getContentType());
        String actualExtension = detectActualExtension.detectFileExtension(filePart);

        if (!actualExtension.equals(fileExtension)) {
            throw new RuntimeException("File extension mismatch");
        }

        if (actualExtension.equals("application/json")
                && (!detectActualExtension.detectJsonAndTextType(filePart))){
            throw new RuntimeException("Not a valid content");
        }
    }
}
