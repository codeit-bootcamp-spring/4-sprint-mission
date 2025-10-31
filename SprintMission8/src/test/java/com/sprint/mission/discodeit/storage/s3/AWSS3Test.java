package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.response.FileResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class AWSS3Test {

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
  private final S3Client s3Client = S3Client.builder()
      .region(Region.of(region))
      .credentialsProvider(StaticCredentialsProvider.create(
          AwsBasicCredentials.create(
              envProps.getProperty("AWS_ACCESS_KEY_ID"),
              envProps.getProperty("AWS_SECRET_ACCESS_KEY")
          )
      ))
      .build();

  @Test
  @DisplayName("S3 업로드 테스트")
  void uploadTest() throws Exception {
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "hello.txt", "text/plain", "Hello S3".getBytes());

    String key = "images/sample.txt";

    PutObjectRequest putReq = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(mockFile.getContentType())
        .build();

    s3Client.putObject(putReq, RequestBody.fromInputStream(mockFile.getInputStream(), mockFile.getSize()));

    HeadObjectResponse head = s3Client.headObject(
        HeadObjectRequest.builder().bucket(bucket).key(key).build()
    );

    assertThat(head).isNotNull();
    System.out.println("Upload Success! Public URL = " + buildPublicUrl(bucket, region, key));
  }

  @Test
  @DisplayName("S3 다운로드 테스트")
  void listFilesTest() {
    List<FileResponseDto> files = list("images", 10);
    assertThat(files).isNotEmpty();
    files.forEach(f -> System.out.println(f.url() + " (" + f.size() + " bytes)"));
  }

  @Test
  @DisplayName("S3 Presigned URL 생성 테스트")
  void presignedUrlTest() throws Exception {
    // Presigned URL 테스트용 업로드
    MockMultipartFile mockFile =
        new MockMultipartFile("file", "hello.txt", "text/plain", "Hello S3".getBytes());
    String key = "images/sample.txt";

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(mockFile.getContentType())
            .build(),
        RequestBody.fromBytes(mockFile.getBytes())
    );

    // Presigner 생성
    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                envProps.getProperty("AWS_ACCESS_KEY_ID"),
                envProps.getProperty("AWS_SECRET_ACCESS_KEY")
            )
        ))
        .build();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest)
        .signatureDuration(Duration.ofMinutes(10))
        .build();

    String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
    System.out.println("Presigned URL = " + presignedUrl);

    assertThat(presignedUrl).isNotBlank();
  }

  private String buildPublicUrl(String bucket, String region, String key) {
    String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
    if (region == null || region.isBlank() || "us-east-1".equals(region)) {
      return "https://" + bucket + ".s3.amazonaws.com/" + encodedKey;
    }
    return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;
  }

  public List<FileResponseDto> list(String prefix, int maxKeys) {
    ListObjectsV2Request req = ListObjectsV2Request.builder()
        .bucket(bucket)
        .prefix(prefix == null ? "" : prefix)
        .maxKeys(maxKeys <= 0 ? 100 : maxKeys)
        .build();

    ListObjectsV2Response res = s3Client.listObjectsV2(req);

    return res.contents().stream()
        .filter(o -> !o.key().endsWith("/"))
        .map(o -> new FileResponseDto(
            o.key(),
            buildPublicUrl(bucket, region, o.key()),
            o.size(),
            o.lastModified()
        ))
        .toList();
  }

  public String toPublicUrl(String key) {
    return buildPublicUrl(bucket, region, key);
  }
}
