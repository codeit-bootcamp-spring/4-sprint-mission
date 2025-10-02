package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Disabled
@ActiveProfiles("test")
@SpringBootTest
@DisplayName("S3BinaryContentStorage 테스트")
class S3BinaryContentStorageTest {

    @Autowired
    private S3BinaryContentStorage s3BinaryContentStorage;

    @Value("${discodeit.storage.s3.bucket}")
    private String bucket;

    @Value("${discodeit.storage.s3.access-key}")
    private String accessKey;

    @Value("${discodeit.storage.s3.secret-key}")
    private String secretKey;

    @Value("${discodeit.storage.s3.region}")
    private String region;

    private final UUID testId = UUID.randomUUID();
    private final byte[] testData = "테스트 데이터".getBytes();


    @Test
    @DisplayName("S3에 파일 업로드 성공 테스트")
    void put_success() {
        // when
        UUID resultId = s3BinaryContentStorage.put(testId, testData);

        // then
        assertThat(resultId).isEqualTo(testId);
    }

    @Test
    @DisplayName("S3에서 파일 다운로드 성공 테스트")
    void get_success() throws IOException {
        //given
        s3BinaryContentStorage.put(testId, testData);

        // when
        InputStream result = s3BinaryContentStorage.get(testId);

        // then
        assertThat(result).isNotNull();
        byte[] bytes = result.readAllBytes();
        assertThat(bytes).isEqualTo(testData);
    }

    @Test
    @DisplayName("Presigned URL 생성 성공 테스트")
    void generatePresignedUrl_success() {
        // when
        String result = s3BinaryContentStorage.generatePresignedUrl(testId.toString(), "application/octet-stream");

        // then
        assertThat(result).isNotNull();
    }

    @AfterEach()
    void cleanup() {
        try{
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .build();

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(testId.toString())
                    .build();

            s3Client.deleteObject(request);
            log.info("clean up 성공");
        } catch (S3Exception e) {
            log.error("clean up 실패 : {}", e.getMessage());
        }
    }
}
