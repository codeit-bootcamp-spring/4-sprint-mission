package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner s3Presigner;
    @Mock
    private PresignedGetObjectRequest presignedGetObjectRequest;

    private S3BinaryContentStorage s3BinaryContentStorage;

    @BeforeEach
    public void setup() {
        // 4개의 String 파라미터만 받는 실제 생성자 사용
        s3BinaryContentStorage = new S3BinaryContentStorage(
                "test-access-key",
                "test-secret-key",
                "test-region",
                "test-bucket"
        );

        // Mock 객체들을 ReflectionTestUtils로 주입
        ReflectionTestUtils.setField(s3BinaryContentStorage, "s3Client", s3Client);
        ReflectionTestUtils.setField(s3BinaryContentStorage, "s3Presigner", s3Presigner);
    }

    @Test
    void put_업로드_성공() {
        // given
        UUID uuid = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();
        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .willReturn(putObjectResponse);

        // when
        UUID binaryContentId = s3BinaryContentStorage.put(uuid, bytes);

        // then
        assertEquals(binaryContentId, uuid);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void put_예외발생시_RuntimeException_던짐() {
        // given
        UUID uuid = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .willThrow(new RuntimeException("S3 업로드 실패"));

        // when & then
        assertThatThrownBy(() -> s3BinaryContentStorage.put(uuid, bytes))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("S3 업로드 실패");
    }

    @Test
    void get_파일의_Id를_통해_InputStream을_얻음() {
        // given
        UUID uuid = UUID.randomUUID();
        ResponseInputStream<GetObjectResponse> mockResponse = mock(ResponseInputStream.class);
        given(s3Client.getObject(any(GetObjectRequest.class)))
                .willReturn(mockResponse);

        // when
        InputStream inputStream = s3BinaryContentStorage.get(uuid);

        // then
        assertNotNull(inputStream);
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    void get_일반예외발생시_RuntimeException_던짐() {
        // given
        UUID uuid = UUID.randomUUID();
        given(s3Client.getObject(any(GetObjectRequest.class)))
                .willThrow(new RuntimeException("S3 조회 실패"));

        // when & then
        assertThatThrownBy(() -> s3BinaryContentStorage.get(uuid))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("S3 조회 실패");
    }

    @Test
    void generatePresignedUrl() throws Exception {
        // given
        String key = "test-key";
        String contentType = "image/jpeg";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test-key";
        given(presignedGetObjectRequest.url()).willReturn(new URL(expectedUrl));
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .willReturn(presignedGetObjectRequest);

        // when
        String presignedUrl = s3BinaryContentStorage.generatePresignedUrl(key, contentType);

        // then
        assertEquals(expectedUrl, presignedUrl);
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void generatePresignedUrl_예외발생시_RuntimeException_던짐() {
        // given
        String key = "test-key";
        String contentType = "image/jpeg";
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .willThrow(new RuntimeException("Presigned URL 생성 실패"));

        // when & then
        assertThatThrownBy(() -> s3BinaryContentStorage.generatePresignedUrl(key, contentType))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("S3 PresignedUrl 생성 실패");
    }

    @Test
    void download_PresignedURL을_생성해_다운로드() throws Exception {
        // given
        UUID uuid = UUID.randomUUID();
        BinaryContentDto binaryContentDto = new BinaryContentDto(
                uuid, "test-file.jpg", 1024L, "image/jpeg"
        );
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test-file";
        given(presignedGetObjectRequest.url()).willReturn(new URL(expectedUrl));
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .willReturn(presignedGetObjectRequest);

        // when
        ResponseEntity<Resource> response = s3BinaryContentStorage.download(binaryContentDto);

        // then
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(expectedUrl, response.getHeaders().getLocation().toString());
        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void getS3Client() {
        // when
        S3Client client = s3BinaryContentStorage.getS3Client();

        // then
        assertEquals(s3Client, client);
    }

    @Test
    void 생성자_필드초기화_확인() {
        // given & when
        S3BinaryContentStorage storage = new S3BinaryContentStorage(
                "access-key", "secret-key", "us-east-1", "test-bucket"
        );

        // then
        assertNotNull(storage);
        assertNotNull(storage.getS3Client());
        // ReflectionTestUtils로 필드값 확인
        assertEquals("access-key", ReflectionTestUtils.getField(storage, "accessKey"));
        assertEquals("secret-key", ReflectionTestUtils.getField(storage, "secretKey"));
        assertEquals("us-east-1", ReflectionTestUtils.getField(storage, "region"));
        assertEquals("test-bucket", ReflectionTestUtils.getField(storage, "bucket"));
    }

    @Test
    void 환경변수_생성자_AWS클라이언트_생성확인() {
        // given & when
        S3BinaryContentStorage storage = new S3BinaryContentStorage(
                "test-access", "test-secret", "ap-northeast-2", "my-bucket"
        );

        // then
        assertNotNull(storage.getS3Client());
        // S3Presigner도 생성되었는지 ReflectionTestUtils로 확인
        Object s3Presigner = ReflectionTestUtils.getField(storage, "s3Presigner");
        assertNotNull(s3Presigner);
    }
}
