package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Client s3Client;
    private final AWSProperties awsProperties;
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    public S3BinaryContentStorage(S3Client s3Client, AWSProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
        this.accessKey = awsProperties.getCredentials().getAccessKey();
        this.secretKey = awsProperties.getCredentials().getSecretKey();
        this.region = awsProperties.getS3().getRegion();
        this.bucket = awsProperties.getS3().getBucket();
    }
        @Override
        public UUID put(UUID id, byte[] bytes) {
            S3Client s3Client = getS3Client();
            s3Client.putObject (
                    builder -> builder.bucket(bucket).key(id.toString()).build(),
                    RequestBody.fromBytes(bytes)
            );
            return id;
        }

        @Override
        public InputStream get(UUID id) {
            S3Client s3Client = getS3Client();
            return s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(id.toString())
                            .build()
            );
        }

        @Override
        public ResponseEntity<byte[]> download(BinaryContentDto dto) {
            // Presigned URL 생성 후 redirect
//            S3Client s3Client = getS3Client();
//            GetObjectRequest objectRequest = GetObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(dto.getId().toString())
//                    .build();
//            ResponseBytes<?> responseBytes = s3Client.getObjectAsBytes(objectRequest);
//            return ResponseEntity.ok()
//                    .header("Content-Type", dto.getContentType())
//                    .body(responseBytes.asByteArray());
            String presignedUrl = generatePresignedUrl(dto.getId(),dto.getContentType());
            return ResponseEntity.status(302)
                    .header("Location",presignedUrl)
                    .build();
        }

        protected S3Client getS3Client() {
            return S3Client.builder()
                    .region(Region.of(region))
                    .endpointOverride(URI.create(awsProperties.getS3().getEndpoint()))
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build()
                    )
                    .build();
        }

        public String generatePresignedUrl(UUID id, String contentType) {
            // S3Presigner로 URL 생성
            S3Presigner presigner = S3Presigner.create();

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(id.toString())
                    .build();

            GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(10))
                    .getObjectRequest(getRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObject = presigner.presignGetObject(presignedRequest);
            return presignedGetObject.url().toString();
        }
}
