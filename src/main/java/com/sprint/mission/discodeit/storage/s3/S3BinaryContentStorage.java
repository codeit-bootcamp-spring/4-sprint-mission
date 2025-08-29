package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

// TODO: 예외처리 할때, 비즈니스 예외로 변환해서 던지시고 전역 예외 처리하는곳에서 처리하도록 변경
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {
    private Properties properties;

    public S3BinaryContentStorage(Properties properties) {
        this.properties = properties;
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String key = binaryContentId.toString();

        try {
            getS3Client().putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.getBucket())
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (SdkClientException e) {
            throw new RuntimeException("S3 upload failed", e);
        }


        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            return getS3Client().getObject(
                    GetObjectRequest.builder()
                            .bucket(properties.getBucket())
                            .key(binaryContentId.toString())
                            .build()
            );
        } catch (SdkClientException e) {
            e.printStackTrace();
            throw new RuntimeException("S3 get failed", e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        String key = metaData.id().toString();
        S3Presigner presigner = generatePresignedUrl();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(properties.getPresignedUrlExpiration()))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(key)
                        .build())
                .build();

        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }


    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.getAccessKey(),
                                        properties.getSecretKey()
                                )
                        )
                )
                .build();
    }

    public S3Presigner generatePresignedUrl() {
        return S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.getAccessKey(),
                                        properties.getSecretKey()
                                )
                        )
                ).build();
    }
}
