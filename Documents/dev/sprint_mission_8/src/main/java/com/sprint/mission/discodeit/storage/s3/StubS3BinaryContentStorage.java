package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StubS3BinaryContentStorage extends S3BinaryContentStorage {
    public StubS3BinaryContentStorage(AWSProperties props) {
        super(null, props); // S3Client 주입 안함
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        // 실제 업로드 동작은 생략하고 호출 여부만 확인
        System.out.println("put() 호출됨" );
        return id;
    }

    @Override
    protected S3Client getS3Client() {
        return new S3Client() {
            @Override
            public void close() {
            }

            @Override
            public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest getObjectRequest) {
                byte[] dummyData = "dummy".getBytes(StandardCharsets.UTF_8);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dummyData);
                AbortableInputStream abortableInputStream = AbortableInputStream.create(byteArrayInputStream);
                return new ResponseInputStream<>(GetObjectResponse.builder().build(), abortableInputStream);
            }

            @Override
            public String serviceName() {
                return "s3";
            }
        };
    }

    @Override
    public ResponseEntity<byte[]> download(BinaryContentDto dto) {
        return ResponseEntity.status(302)
                .header("Location", "https://stub-url.com/dummy")
                .build();
    }

    @Override
    public String generatePresignedUrl(UUID id, String fileName) {
        return "https://stub-url.com/" + fileName;
    }
}
