package com.lulobank.otp.services.ports.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

public interface FileService {
    S3ObjectInputStream getFile(String key);
}
