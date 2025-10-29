package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Disabled
public class AWSS3Test {

    private static String accessKey;
    private static String secretKey;
    private static String region;
    private static String bucketName;

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String testKey;

    @BeforeAll
    static void loadProperties() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
            accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
            secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
            region = properties.getProperty("AWS_S3_REGION");
            bucketName = properties.getProperty("AWS_S3_BUCKET");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        testKey = UUID.randomUUID().toString();

        s3Client = S3Client.builder()
                .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                        accessKey,
                                        secretKey
                                    )
                            )
                    )
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                        accessKey,
                                        secretKey
                                    )
                            )
                    )
                .build();
    }

    @Test
    @DisplayName("S3에 파일 업로드를 성공한다")
    void uploadFileToS3_Success() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());


        //when & then
        try{
            PutObjectRequest request =  PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("파일 업로드 성공");
        } catch(S3Exception e) {
            log.error("파일 업로드 실패 : {}", e.getMessage());
        }

    }

    @Test
    @DisplayName("S3 파일 다운로드 테스트 성공")
    void downloadFileFromS3_Success() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

        try{
            PutObjectRequest request =  PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("파일 업로드 성공");
        } catch(S3Exception e) {
            log.error("파일 업로드 실패 : {}", e.getMessage());
        }

        //when & then
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build();

            String result =s3Client.getObject(request).toString();
            log.info("파일 다운로드 성공 : {}", result);
        } catch (S3Exception e) {
            log.error("파일 다운로드 실패 : {}", e.getMessage());
        }

    }

    @Test
    @DisplayName("PresignedUrl 생성 테스트")
    void createPresignedUrl_Success() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());

        try{
            PutObjectRequest uploadRequest =  PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(uploadRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("파일 업로드 성공");
        } catch(S3Exception e) {
            log.error("파일 업로드 실패 : {}", e.getMessage());
        }

        //when & then
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build();

            GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(request)
                    .build();

            PresignedGetObjectRequest presignedUrl = s3Presigner.presignGetObject(presignedRequest);

            log.info("생성된 presignedUrl : {}", presignedUrl.url());
        } catch (S3Exception e) {
            log.error("presignedUrl 생성 실패 {}", e.getMessage());
        }

    }

    @AfterEach
    void cleanup() {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build();
            s3Client.deleteObject(request);
            log.info("파일 삭제 성공");
        } catch (S3Exception e) {
            log.error("파일 삭제 실패 : {}", e.getMessage());
        }
    }


}
