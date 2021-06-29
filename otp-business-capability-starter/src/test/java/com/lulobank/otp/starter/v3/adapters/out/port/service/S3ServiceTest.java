package com.lulobank.otp.starter.v3.adapters.out.port.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.lulobank.otp.starter.v3.adapters.out.service.fileservice.FileServiceAdapter;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class S3ServiceTest {

    @Test
    public void getFileFromS3() {
        AmazonS3 amazonS3 = mock(AmazonS3.class);
        S3Object s3Object = new S3Object();
        s3Object.setBucketName("bucketName");
        s3Object.setKey("FilePath.pdf");
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(
                IOUtils.toInputStream("123", StandardCharsets.UTF_8),
                mock(HttpRequestBase.class));
        s3Object.setObjectContent(s3ObjectInputStream);
        when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3Object);
        FileServiceAdapter fileServiceAdapter =
                new FileServiceAdapter(amazonS3, "nameBucket");
        S3ObjectInputStream objectInputStream = fileServiceAdapter.getFile("FilePath.pdf");
        Assert.assertNotNull(objectInputStream);
    }
}
