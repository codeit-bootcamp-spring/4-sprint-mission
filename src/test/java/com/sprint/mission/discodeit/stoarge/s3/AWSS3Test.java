package com.sprint.mission.discodeit.stoarge.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class AWSS3Test {

  private static String bucket;
  private static S3Client s3;
  private static S3Presigner s3Presigner;

  @BeforeAll
  static void setUp() throws IOException {

    Properties prop = new Properties();
    prop.load(new FileInputStream(".env"));

    String accessKeyId = prop.getProperty("AWS_S3_ACCESS_KEY");
    String secretAccessKey = prop.getProperty("AWS_S3_SECRET_KEY");
    String region = prop.getProperty("AWS_S3_REGION");
    bucket = prop.getProperty("AWS_S3_BUCKET");

    //S3Client 생성
    AwsBasicCredentials creds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

    s3 = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(creds))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(creds))
        .build();
  }

  @Test
  void testUploadFile() {
    String key = "test/upload-sample.txt";
    String filePath = "src/test/resources/upload-sample.txt";

    s3.putObject(PutObjectRequest
        .builder()
        .bucket(bucket)
        .key(key)
        .build(), Paths.get(filePath));

    System.out.println("업로드 완료 : " + key);
  }

  @Test
  void testDownloadFile() {
    String key = "test/upload-sample.txt";
    String filePath = "src/test/resources/download-sample.txt";

    s3.getObject(GetObjectRequest
        .builder()
        .bucket(bucket)
        .key(key)
        .build(), Paths.get(filePath));

    System.out.println("다운로드 완료 : " + key);
  }

  @Test
  void testPresignedUrl(){
    String key = "test/upload-sample.txt";

    GetObjectRequest getObjectRequest = GetObjectRequest
        .builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    String url = s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    System.out.println("Presigned URL : " + url);
  }
}
