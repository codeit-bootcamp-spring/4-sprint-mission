package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binary_content.BinaryContentException;
import com.sprint.mission.discodeit.exception.binary_content.BinaryContentUploadFailException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final BinaryContentService binaryContentService;
    private final ApplicationEventPublisher eventPublisher;
    private final S3Client s3Client;
    private final S3Presigner presigner;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;

    public S3BinaryContentStorage(String accessKey, String secretKey, String region, String bucket, BinaryContentService binaryContentService, ApplicationEventPublisher eventPublisher) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        if (accessKey != null && !accessKey.isBlank()) {
            s3Client = S3Client.builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))).build();
        } else {
            s3Client = S3Client.builder().region(Region.of(region)).credentialsProvider(DefaultCredentialsProvider.create()).build();
        }
        presigner = S3Presigner.builder().region(Region.of(region)).credentialsProvider((accessKey != null && !accessKey.isBlank()) ? StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)) : DefaultCredentialsProvider.create()).build();
        this.binaryContentService = binaryContentService;
        this.eventPublisher = eventPublisher;
    }

    @Retryable(
            retryFor = {BinaryContentUploadFailException.class, Throwable.class, AwsServiceException.class, SdkClientException.class},
            recover = "recover",
            backoff = @Backoff(delay = 1000)
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        log.info("Put binary content with id {}", binaryContentId);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while simulating delay", e);
        }

        String key = binaryContentId.toString();

        // 2) PutObjectRequest 생성 (버킷 정책이 퍼블릭 읽기면 acl 생략해도 됨)
        PutObjectRequest putReq = PutObjectRequest.builder().bucket(bucket).key(key)
                // .acl(ObjectCannedACL.PUBLIC_READ) // 필요 시 주석 해제
                .build();

        // 3) 업로드 (임시파일 없이 InputStream으로)
        try {
            s3Client.putObject(putReq, RequestBody.fromBytes(bytes));
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
            log.info("S3 업로드 성공");
        } catch (AwsServiceException e) {
            throw new BinaryContentUploadFailException(ErrorCode.BINARY_CONTENT_UPLOAD_FAIL, Map.of("RequestId", MDC.get("requestId"),
                    "BinaryContentId", binaryContentId.toString()));
        } catch (SdkClientException e) {
            throw new BinaryContentUploadFailException(ErrorCode.BINARY_CONTENT_UPLOAD_FAIL, Map.of("RequestId", MDC.get("requestId"), "BinaryContentId", binaryContentId.toString()));
        } catch (Exception e) {
            throw new BinaryContentUploadFailException(ErrorCode.BINARY_CONTENT_UPLOAD_FAIL, Map.of("RequestId", MDC.get("requestId"), "BinaryContentId", binaryContentId.toString()));
        }

        // 4) 퍼블릭 URL 생성 후 반환
        return binaryContentId;
        // ⚠ 주입받은 s3Client는 닫지 말 것 (Bean 공용)
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        // 응답 헤더(Content-Disposition)를 presign 시점에 주입
        GetObjectRequest getReq = GetObjectRequest.builder().bucket(bucket).key(binaryContentId.toString()).responseContentDisposition("attachment; filename=\"" + binaryContentId.toString() + "\"").build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder().getObjectRequest(getReq).signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        URL signed = presigner.presignGetObject(preReq).url();
        try {
            return signed.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity download(BinaryContentDto binaryContentDto) {
        String presignedUrl = generatePresignedUrl(binaryContentDto.id().toString(), binaryContentDto.contentType());
        return ResponseEntity.status(302).location(URI.create(presignedUrl)).build();
    }

    private S3Client getS3Client() {
        return s3Client;
    }

    private String generatePresignedUrl(String key, String contentType) {
        GetObjectRequest getReq = GetObjectRequest.builder().bucket(bucket).key(key).responseContentType(contentType).build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        String signed = presigner.presignGetObject(preReq).url().toString();

        return signed;
    }

    @Recover
    public UUID recover(BinaryContentException e, UUID binaryContentId, byte[] bytes) {
        log.info("S3 업로드 실패");
        eventPublisher.publishEvent(new S3UploadFailedEvent(e, binaryContentId));
        throw new BinaryContentUploadFailException(ErrorCode.BINARY_CONTENT_UPLOAD_FAIL, Map.of("RequestId", e.getMessage()));
    }

}
