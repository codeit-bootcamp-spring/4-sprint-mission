package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

  private static final Properties envProps = new Properties();

  static {
    try (FileInputStream fis = new FileInputStream(".env")) {
      envProps.load(fis);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load .env file", e);
    }
  }

  private final String bucket = envProps.getProperty("S3_BUCKET_NAME");
  private final String region = envProps.getProperty("AWS_REGION");
  private final S3Client s3Client;
  private final S3Presigner presigner;

  public S3BinaryContentStorage() {
    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                envProps.getProperty("AWS_ACCESS_KEY_ID"),
                envProps.getProperty("AWS_SECRET_ACCESS_KEY")
            )
        ))
        .build();

    this.presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                envProps.getProperty("AWS_ACCESS_KEY_ID"),
                envProps.getProperty("AWS_SECRET_ACCESS_KEY")
            )
        ))
        .build();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();
    PutObjectRequest putReq = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(putReq, RequestBody.fromBytes(bytes));
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();
    return s3Client.getObject(GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build());
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    String presignedUrl = generatePresignedUrl(metaData.id().toString(), metaData.contentType());
    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  public String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
        .getObjectRequest(getReq)
        .signatureDuration(Duration.ofMinutes(10))
        .build();

    return presigner.presignGetObject(presignReq).url().toString();
  }

  public S3Client getS3Client() {
    return this.s3Client;
  }
}
