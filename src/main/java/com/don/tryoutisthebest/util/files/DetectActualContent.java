package com.don.tryoutisthebest.util.files;

import com.don.tryoutisthebest.util.minio.MinioUtil;
import lombok.RequiredArgsConstructor;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class DetectActualContent {
    private final GetMime mime;
    private final MinioUtil minioService;

    public boolean detectJsonAndTextType(FilePart filePart) {

        String json = mime.getMime(filePart);
        boolean valid = true;
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            valid = false;
        }

        return valid;
    }

    public String detectFileExtension(FilePart filePart) throws TikaException, IOException {
        InputStream inputStreamFromFluxDataBuffer = minioService.getInputStreamFromFluxDataBuffer(filePart.content());

        TikaConfig tc = new TikaConfig();
        Metadata md = new Metadata();

        md.set(TikaCoreProperties.RESOURCE_NAME_KEY, filePart.filename());
        return tc.getDetector().detect(TikaInputStream.get(inputStreamFromFluxDataBuffer), md).toString();
    }
}
