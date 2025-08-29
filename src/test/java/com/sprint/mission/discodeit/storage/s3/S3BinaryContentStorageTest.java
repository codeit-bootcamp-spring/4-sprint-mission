package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3BinaryContentStorageTest {

    private Properties properties;
    private S3BinaryContentStorage s3BinaryContentStorage;
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        // given
        properties = new Properties();
        properties.setBucket("test-bucket");
        properties.setRegion("ap-northeast-2");
        properties.setAccessKey("access-key");
        properties.setSecretKey("secret-key");
        properties.setPresignedUrlExpiration(600L);

        s3Client = mock(S3Client.class);

        s3BinaryContentStorage = new S3BinaryContentStorage(properties) {
            @Override
            public S3Client getS3Client() {
                return s3Client;
            }
        };
    }

    @Test
    @DisplayName("bucket, key 확인 및 반환 값 검증")
    void putSuccess() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = new byte[1024];

        // when
        UUID result = s3BinaryContentStorage.put(binaryContentId, bytes);

        // then
        assertEquals(binaryContentId, result, "put 메서드는 입력한 UUID를 그대로 반환해야 함");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client, times(1)).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertEquals("test-bucket", capturedRequest.bucket(), "버킷 이름 동일해야 함");
        assertEquals(binaryContentId.toString(), capturedRequest.key(), "S3 key는 UUID 문자열이어야 험");
    }

    @Test
    @DisplayName("put 실패 시 RuntimeException 발생 테스트")
    void putShouldThrowRuntimeExceptionWhenS3Fail() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = new byte[1024];

        doThrow(new RuntimeException("S3 upload failed"))
                .when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // when & then
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> s3BinaryContentStorage.put(binaryContentId, bytes)
        );
        assertEquals("S3 upload failed", exception.getMessage());
    }


    @Test
    void getSuccess() throws IOException {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "테스트 데이터".getBytes();

        ResponseInputStream<GetObjectResponse> mockResponseInputStream =
                new ResponseInputStream<>(
                        GetObjectResponse.builder().contentLength((long) bytes.length).build(),
                        new ByteArrayInputStream(bytes)
                );

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(mockResponseInputStream);

        // when
        InputStream inputStream = s3BinaryContentStorage.get(binaryContentId);
        byte[] actualData = inputStream.readAllBytes();

        // then
        assertNotNull(inputStream);
        assertArrayEquals(bytes, actualData);
    }

    @Test
    @DisplayName("get 실패 테스트 - S3 예외 발생")
    void getShouldThrowRuntimeExceptionWhenS3Fail() {
        UUID binaryContentId = UUID.randomUUID();

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(SdkClientException.builder().message("S3 get failed").build());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> s3BinaryContentStorage.get(binaryContentId));

        assertEquals("S3 get failed", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("다운로드 성공 - presignedUrl 반환")
    void downloadSuccess() throws MalformedURLException {
        // given
        UUID binaryContentId = UUID.randomUUID();
        BinaryContentDto metaData = new BinaryContentDto(
                binaryContentId,
                "test.txt",
                1024L,
                "text/plain"
        );

        S3Presigner mockPresigner = mock(S3Presigner.class);
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);

        when(mockPresignedRequest.url()).thenReturn(new URL("https://s3.mock/test.txt"));
        when(mockPresigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(mockPresignedRequest);

        // S3BinaryContentStorage 익명클래스 오버라이드
        S3BinaryContentStorage storage = new S3BinaryContentStorage(properties) {
            @Override
            public S3Presigner generatePresignedUrl() {
                return mockPresigner;
            }
        };

        // when
        var response = storage.download(metaData);

        // then
        assertNotNull(response, "ResponseEntity는 null이면 안 됨");
        assertEquals(302, response.getStatusCode().value(), "HTTP 상태코드는 302 FOUND 이어야 함");
        assertEquals("https://s3.mock/test.txt",
                response.getHeaders().getLocation().toString(),
                "Location 헤더에 presigned URL이 담겨야 함");
    }
}