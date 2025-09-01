package com.codeit.discodeit8.storage.s3;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit8.exception.binarycontent.BinaryContentNotFoundException;
import com.codeit.discodeit8.exception.binarycontentstorage.BinaryContentStorageFailReadFileException;
import com.codeit.discodeit8.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  @Getter
  private final S3Client s3Client;


  private final S3Presigner presigner;

  private final String prefix ="uploads/";

  public S3BinaryContentStorage(String accessKey, String secretKey, String region, String bucket) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;

    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(creds())
        .build();

    this.presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(creds())
        .build();
  }

  // aws 자격 증명
  private AwsCredentialsProvider creds() {
    return (accessKey != null && !accessKey.isBlank())
        ? StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
        : DefaultCredentialsProvider.create();
  }

  // 파일 업로드
  public UUID put(UUID binaryContentId, byte[] bytes) {
    // key 설정
    String key = prefix+binaryContentId.toString();

    // 업로드 전 로그
    log.info("[S3] PUT start: bucket={}, key={}, size={}", bucket, key, bytes.length);

    // PutObjectRequest 생성
    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket) // 클래스 필드 또는 설정에서 주입된 bucket
        .key(key)
        .contentType("application/octet-stream")
        .contentLength((long) bytes.length)
        .build();

    try {
      // 업로드 실행
      PutObjectResponse response = s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));
      log.info("[S3] PUT success: key={}, eTag={}", key, response.eTag());
      return binaryContentId;

    } catch (S3Exception e) {
      log.error("[S3] PUT failed (S3Exception): status={}, code={}, msg={}, requestId={}",
          e.statusCode(),
          e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null,
          e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage(),
          e.requestId(), e);
      throw new RuntimeException("S3 업로드 실패(S3Exception): " + key, e);

    } catch (SdkClientException e) {
      log.error("[S3] PUT failed (SdkClientException): msg={}", e.getMessage(), e);
      throw new RuntimeException("S3 업로드 실패(SdkClientException): " + key, e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    return null; // 여기선 무슨 기능을 해야하지?
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    String rawKey = dto.id().toString();
    String contentType = dto.contentType();
    String url = generatePresignedUrl(rawKey, contentType);
    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, url)
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    String objectKey = prefix + key;
    String ct = (contentType == null || contentType.isBlank())
        ? "application/octet-stream" : contentType;

    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(objectKey)
        .responseContentType(ct)
        .build();

    GetObjectPresignRequest psReq = GetObjectPresignRequest.builder()
        .getObjectRequest(getReq)
        .signatureDuration(Duration.ofMinutes(10))
        .build();

    return presigner.presignGetObject(psReq).url().toString();
  }
}