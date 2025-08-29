package com.sprint.mission.discodeit.storage.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
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
import java.net.URI;
import java.time.Duration;
import java.util.Properties;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class AWSS3Test {
    private Properties props = new Properties();
    private S3Client s3Client;
    private S3Presigner presigner;

    @PostConstruct
    public void init() {
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!props.isEmpty() && props.size() > 0) {
            if (props.getProperty("AWS_S3_ACCESS_KEY") != null && !props.getProperty("AWS_S3_ACCESS_KEY").isBlank()) {
                s3Client = S3Client.builder()
                        .region(Region.of(props.getProperty("AWS_S3_REGION")))
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create(
                                                props.getProperty("AWS_S3_ACCESS_KEY"),
                                                props.getProperty("AWS_S3_SECRET_KEY")
                                        )
                                )
                        )
                        .build();
            } else {
                s3Client = S3Client.builder()
                        .region(Region.of(props.getProperty("AWS_S3_REGION")))
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
            }

            presigner = S3Presigner.builder()
                    .region(Region.of(props.getProperty("AWS_S3_REGION")))
                    .credentialsProvider(
                            (props.getProperty("AWS_S3_ACCESS_KEY") != null && !props.getProperty("AWS_S3_ACCESS_KEY").isBlank())
                                    ? StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                    props.getProperty("AWS_S3_ACCESS_KEY"),
                                    props.getProperty("AWS_S3_SECRET_KEY")))
                                    : DefaultCredentialsProvider.create()
                    )
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        String key = file.getOriginalFilename();
        key = key.replace(" ", "_");

        // 2) PutObjectRequest 생성 (버킷 정책이 퍼블릭 읽기면 acl 생략해도 됨)
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(props.getProperty("AWS_S3_BUCKET"))
                .key(key)
                .contentType(file.getContentType())
                // .acl(ObjectCannedACL.PUBLIC_READ) // 필요 시 주석 해제
                .build();

        // 3) 업로드 (임시파일 없이 InputStream으로)
        try {
            s3Client.putObject(putReq,
                    RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(key);
    }

    @GetMapping
    public ResponseEntity download(@RequestParam("url") String url) {
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }

    @PostMapping(value = "/url")
    public ResponseEntity createPresignedUrl(@RequestParam("filename") String filename) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(props.getProperty("AWS_S3_BUCKET"))
                .key(filename)
                .build();

        GetObjectPresignRequest preReq = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofMinutes(5)) // 유효기간
                .build();

        String signed = presigner.presignGetObject(preReq).url().toString();

        return ResponseEntity.ok(signed);
    }

}
