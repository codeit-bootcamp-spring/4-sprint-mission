package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AWSS3Test {
    private static Properties props = new Properties();
    private static S3Client s3Client;
    private static S3Presigner presigner;

    @BeforeAll
    public static void setup() {
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!props.isEmpty() && props.size() > 0) {
            if (props.getProperty("AWS_S3_ACCESS_KEY") != null && !props.getProperty("AWS_S3_ACCESS_KEY").isBlank()) {
                s3Client = S3Client.builder()
                        .region(Region.of(props.getProperty("AWS_S3_REGION")))
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create(
                                                props.getProperty("AWS_S3_ACCESS_KEY"),
                                                props.getProperty("AWS_S3_SECRET_KEY")
                                        )
                                )
                        )
                        .build();
            } else {
                s3Client = S3Client.builder()
                        .region(Region.of(props.getProperty("AWS_S3_REGION")))
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
            }

            presigner = S3Presigner.builder()
                    .region(Region.of(props.getProperty("AWS_S3_REGION")))
                    .credentialsProvider(
                            (props.getProperty("AWS_S3_ACCESS_KEY") != null && !props.getProperty("AWS_S3_ACCESS_KEY").isBlank())
                                    ? StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                    props.getProperty("AWS_S3_ACCESS_KEY"),
                                    props.getProperty("AWS_S3_SECRET_KEY")))
                                    : DefaultCredentialsProvider.create()
                    )
                    .build();
        }
    }


    @Test
    void upload() throws IOException {
        // 본문 생성
        MockMultipartFile file = new MockMultipartFile("file.txt", "test_content".getBytes());
        // given
        String key = "test-" + UUID.randomUUID();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(props.getProperty("AWS_S3_BUCKET"))
                .key(key)
                .contentType(file.getContentType())
                .build();

        // when
        s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(file.getBytes()));

        var result = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(props.getProperty("AWS_S3_BUCKET"))
                        .key(key)
                        .build()
        );
        // then
        assertArrayEquals("test_content".getBytes(), result.readAllBytes());
    }

    @Test
    void download() throws IOException {
        // given
        String url = "test-715e1e52-81e8-4434-a64e-f2798f3970a0";

        // when
        var result = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(props.getProperty("AWS_S3_BUCKET"))
                        .key(url)
                        .build()
        );

        // then
        assertArrayEquals("test_content".getBytes(), result.readAllBytes());
    }

    @Test
    void createPresignedUrl() {
        // 본문생성
        String filename = "file.txt";

        // given
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(props.getProperty("AWS_S3_BUCKET"))
                .key(filename)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(1))
                .build();

        // when
        String signed = presigner.presignGetObject(getObjectPresignRequest).url().toString();

        // then
        assertNotNull(signed);
        assertNotEquals(filename, signed);
    }

    @DisplayName("download 시 사용되는 key값이 존재하지 않을 시 예외를 발생시킨다.")
    @Test
    void downloadShouldFailedWhenUnknownKeyValue() {
        // given
        String url = "failed";

        // when & then
        Exception exception = assertThrows(Exception.class, () -> s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(props.getProperty("AWS_S3_BUCKET"))
                        .key(url)
                        .build()
        ));
        assertInstanceOf(NoSuchKeyException.class, exception);
    }

    @DisplayName("Bucket이 올바르지 않으면 예외가 발생한다.")
    @Test
    void shouldFailedWhenInvalidBucket() {
        // given
        String bucket = "invalidBucket";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key("key")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .build();

        // when & then
        Exception exception = assertThrows(Exception.class,
                () -> s3Client.putObject(putObjectRequest,
                        RequestBody.fromBytes("failed".getBytes())));
        assertInstanceOf(NoSuchBucketException.class, exception);
    }
}
