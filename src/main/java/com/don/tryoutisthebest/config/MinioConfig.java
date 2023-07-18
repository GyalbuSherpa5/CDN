package com.don.tryoutisthebest.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioConfig {
    /**
     * It's a URL, domain name ,IPv4 perhaps IPv6 Address ")
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * //"TCP/IP Port number "
     */

    @Value("${minio.port}")
    private Integer port;

    /**
     * //"accessKey Similar to user ID, Used to uniquely identify your account "
     */

    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * //"secretKey It's the password for your account "
     */

    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * //" If it is true, It uses https instead of http, The default value is true"
     */

    @Value("${minio.secure}")
    private boolean secure;

    /**
     * //" Default bucket "
     */

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * //" temp bucket "
     */

    @Value("${minio.temp-bucket}")
    private String tempBucket;

    /**
     * The maximum size of the picture
     */

    @Value("${minio.image-size}")
    private long imageSize;

    /**
     * Maximum size of other files
     */

    @Value("${minio.file-size}")
    private long fileSize;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().credentials(accessKey, secretKey).endpoint(endpoint, port, secure).build();
    }
}


