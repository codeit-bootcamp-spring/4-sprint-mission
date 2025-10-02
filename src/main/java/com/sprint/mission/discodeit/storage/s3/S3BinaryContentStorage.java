package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;

    private final String secretKey;

    private final String region;

    private final String bucket;

    @Value("${discodeit.storage.s3.presigned-url-expiration}")
    private long presignedUrlexpiration;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        S3Client s3Client = getS3Client();
        String key = binaryContentId.toString();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
            log.info("파일 업로드 성공");
            return binaryContentId;
        } catch (S3Exception e) {
            log.error("파일 업로드 실패");
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            S3Client s3Client = getS3Client();

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(binaryContentId.toString())
                    .build();
//            byte[] bytes = s3Client.getObjectAsBytes(request).asByteArray();
//            return new ByteArrayInputStream(bytes);
            return s3Client.getObject(request);
        } catch (S3Exception e) {
            log.error("파일 다운로드 실패 : {}", e.getMessage());
        }
        return null;
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentResponse response) {
        String presignedUrl = generatePresignedUrl(response.getId().toString(), response.getContentType());

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", presignedUrl)
                .build();
    }

    public S3Client getS3Client() {
        return S3Client.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey, secretKey
                                )
                        ))
                .region(Region.of(region))
                .build();
    }

    public String generatePresignedUrl(String key, String contentType) {
        S3Presigner s3Presigner = S3Presigner.builder()
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


        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(request)
                .build();

        PresignedGetObjectRequest presignedUrl = s3Presigner.presignGetObject(presignedRequest);
        return presignedUrl.url().toString();
    }

}
