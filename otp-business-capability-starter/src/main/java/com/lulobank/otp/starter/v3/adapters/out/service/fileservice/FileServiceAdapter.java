package com.lulobank.otp.starter.v3.adapters.out.service.fileservice;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.lulobank.otp.services.ports.service.FileService;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FileServiceAdapter implements FileService {

    private AmazonS3 amazonS3;
    private String evidenceBucketName;

    @Override
    public S3ObjectInputStream getFile(String key) {
        return getDocumentsFromS3(key);
    }

    public S3ObjectInputStream getDocumentsFromS3(String key) {
        log.info("Reading files from {} bucket", evidenceBucketName);
        return Try.of(() -> amazonS3.getObject(evidenceBucketName, key))
                .peek(s3Object -> log.info("Reading {} file ", s3Object.getKey()))
                .map(S3Object::getObjectContent)
                .onFailure(t -> log.error("Failed to read {} {}", key, t.getMessage()))
                .get();
    }

}
