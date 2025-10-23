package com.sprint.mission.discodeit.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.AWSProperties;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.StubS3BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S3BinaryContentStorageTest {

    private AWSProperties awsProperties;
    private S3BinaryContentStorage storage;

    @BeforeEach
    void setUp() {
        AWSProperties properties = new AWSProperties();
        properties.getS3().setRegion("ap-northeast-2");
        AWSProperties.S3 s3 = new AWSProperties.S3();
        s3.setBucket("test-bucket");
        s3.setEndpoint("http://localhost:8081");
        properties.setS3(s3);

        this.awsProperties = properties;

        this.storage = new StubS3BinaryContentStorage(properties);
    }

    private final UUID testId = UUID.randomUUID();
    private final String testContent = "Hello, Test!";
    private final String testContentType = "text/plain";

    @Test
    void testPut() {
//        //given
        UUID id = UUID.randomUUID();
        byte[] data = testContent.getBytes(StandardCharsets.UTF_8);
//        //when
        UUID result = storage.put(id, data);
//        //then
        assertNotNull(result);
        assertEquals(id, result);

    }

    @Test
    void testGet() throws IOException {
        //given
        UUID id = UUID.randomUUID();
        InputStream result = storage.get(id);
        assertNotNull(result);
        byte[] downloaded = result.readAllBytes();
        assertEquals("dummy", new String(downloaded, StandardCharsets.UTF_8));
    }

    @Test
    void testDownload() {
        //given
        byte[] data = "Hello, Test!".getBytes(StandardCharsets.UTF_8);
        BinaryContentDto dto = new BinaryContentDto(
                testId, "test", 122L, "test.txt", data
        );
        //when & then
        ResponseEntity<byte[]> result = storage.download(dto);
        assertEquals(302, result.getStatusCodeValue());
        assertNotNull(result.getHeaders().getLocation());
    }

    @Test
    void testGeneratePresignedUrl() {
        UUID id = UUID.randomUUID();

        assertDoesNotThrow(() -> {
            String url = storage.generatePresignedUrl(id, "test");
            assertNotNull(url);
        });

    }
}
