package com.lulobank.otp.starter.v3.adapters.out.service.fileservice;

import com.amazonaws.services.s3.AmazonS3;
import com.lulobank.otp.services.ports.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceConfig {

    @Value("${digitalevidence.s3.evidence}")
    private String bucketEvidence;

    @Bean
    public FileService getFileService(AmazonS3 amazonS3) {
        return new FileServiceAdapter(amazonS3, bucketEvidence);
    }
}
