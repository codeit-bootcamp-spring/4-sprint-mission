package com.sprint.mission.discodeit.storage.s3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Component
public class AWSS3Test implements CommandLineRunner {

    private final Properties properties;

    public AWSS3Test(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void run(String... args) throws Exception {
        String bucket = properties.getBucket();

        // 0. 테스트용 파일 생성
        Path testFile = Paths.get("test.txt");
        Files.writeString(testFile, "S3 업로드 테스트용 파일");

        // 1. S3 클라이언트 생성
        S3Client s3 = S3Client.builder()
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

        // 2. 업로드 테스트
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key("test.txt")
                        .build(),
                testFile
        );

        // 3. 다운로드 테스트
        Path downloadedFile = Paths.get("test-downloaded.txt");
        s3.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key("test.txt")
                        .build(),
                downloadedFile
        );

        // 4. Presigned URL 생성 테스트
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        properties.getAccessKey(),
                                        properties.getSecretKey()
                                )
                        )
                ).build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key("test.txt")
                        .build())
                .build();

        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
        System.out.println("Presigned URL: " + presignedUrl);

        System.out.println("S3 업로드/다운로드/Presigned URL 테스트 완료");
    }
}
