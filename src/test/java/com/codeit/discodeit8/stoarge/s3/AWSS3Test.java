package com.codeit.discodeit8.stoarge.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codeit.discodeit8.config.AwsProperties;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@SpringBootTest(properties = {
    "discodeit.storage.type=s3"
})
class AWSS3Test {

  @Autowired
  private AwsProperties awsProperties;

  @Autowired
  private S3Client s3Client;

  private String bucket() {
    return awsProperties.getS3().getBucket();
  }

  static Dotenv dotenv = Dotenv.configure()
      .directory(".")
      .ignoreIfMalformed()
      .ignoreIfMissing()
      .load();

  @DynamicPropertySource
  static void loadEnvProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.credentials.access-key", () -> dotenv.get("AWS_ACCESS_KEY"));
    registry.add("aws.credentials.secret-key", () -> dotenv.get("AWS_SECRET_KEY"));
    registry.add("aws.region", () -> dotenv.get("AWS_REGION"));
    registry.add("aws.s3.bucket", () -> dotenv.get("AWS_S3_BUCKET"));
  }

  @Test
  void testAwsPropertiesLoaded() {
    assertNotNull(awsProperties.getCredentials().getAccessKey());
    assertNotNull(awsProperties.getCredentials().getSecretKey());
    assertNotNull(awsProperties.getRegion());
    assertNotNull(awsProperties.getS3().getBucket());

    System.out.println("Access Key: " + awsProperties.getCredentials().getAccessKey());
    System.out.println("Region: " + awsProperties.getRegion());
    System.out.println("Bucket: " + awsProperties.getS3().getBucket());
  }

  /** ================= Upload Test ================= */
  @Test
  void testUpload() {
    String key = "test-upload.txt";
    String content = "Hello S3!";

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket())
        .key(key)
        .build();

    s3Client.putObject(putRequest, RequestBody.fromString(content));

    // 업로드 확인
    HeadObjectRequest headRequest = HeadObjectRequest.builder()
        .bucket(bucket())
        .key(key)
        .build();
    HeadObjectResponse response = s3Client.headObject(headRequest);

    assertNotNull(response);
  }

  /** ================= Download Test ================= */
  @Test
  void testDownload() throws IOException {
    String key = "test-upload.txt";

    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket())
        .key(key)
        .build();

    byte[] downloaded = s3Client.getObject(getRequest).readAllBytes();
    String content = new String(downloaded);

    assertEquals("Hello S3!", content);
  }

  /** ================= Presigned URL Test ================= */
  @Test
  void testPresignedUrl() {
    String key = "test-upload.txt";

    // Presigner 생성
    var presigner = software.amazon.awssdk.services.s3.presigner.S3Presigner.create();

    var getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket())
        .key(key)
        .build();

    var presignRequest = software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest)
        .signatureDuration(Duration.ofMinutes(10))
        .build();

    URL presignedUrl = presigner.presignGetObject(presignRequest).url();

    System.out.println("Presigned URL: " + presignedUrl);
    assertNotNull(presignedUrl);
  }
}
