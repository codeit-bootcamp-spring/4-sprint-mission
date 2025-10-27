package com.sprint.mission.discodeit.s3;

import com.sprint.mission.discodeit.storage.s3.AWSProperties;
import com.sprint.mission.discodeit.storage.s3.AWSS3Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties(AWSProperties.class)
public class AWSS3Test {

    @Autowired
    private AWSProperties awsProperties;

    private final String bucket = "discodeit-binary-content-storage-mes";
    private final String testKey = "test/sample.txt";
    private final String testContent = "Hello S3!";

    @Test
    void testUploadProperties() {
        assertNotNull(awsProperties.getS3().getBucket());
        assertNotNull(awsProperties.getS3().getRegion());
        assertNotNull(awsProperties.getCredentials().getAccessKey());
        assertNotNull(awsProperties.getCredentials().getSecretKey());
        System.out.println("Upload Test - Bucket: " + awsProperties.getS3().getBucket());
    }

    @Test
    void testDownloadProperties() {
        assertEquals("ap-northeast-2", awsProperties.getS3().getRegion());
        System.out.println("Download Test - Region: " + awsProperties.getS3().getRegion());
    }

    @Test
    void testPresignedUrlProperties() {
        assertTrue(awsProperties.getS3().getEndpoint().startsWith("https://"));
        System.out.println("Presigned URL Test - Endpoint: " + awsProperties.getS3().getEndpoint());
    }
}
