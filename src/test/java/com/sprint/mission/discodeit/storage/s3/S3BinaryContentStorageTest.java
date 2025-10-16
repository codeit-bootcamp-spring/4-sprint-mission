package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {
    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner presigner;
    @Mock
    private PresignedGetObjectRequest presignedGetObjectRequest;

    private S3BinaryContentStorage s3BinaryContentStorage;

    @BeforeEach
    public void setup() {
        s3BinaryContentStorage = new S3BinaryContentStorage(s3Client, presigner, "accessKey", "secretKey", "region", "bucket");
    }

    @DisplayName("AWS S3에 파일을 업로드합니다. binaryContentController에서 파일을 받고 binaryContent를 생성하고 해당 ID와 bytes로 처리합니다.")
    @Test
    void put() {
        // given
        UUID uuid = UUID.randomUUID();
        byte[] bytes = "test".getBytes();

        // when
        UUID binaryContentId = s3BinaryContentStorage.put(uuid, bytes);

        // then
        assertEquals(binaryContentId, uuid);
    }

    @DisplayName("파일의 Id를 통해 InputStream을 얻습니다.")
    @Test
    void get() throws IOException {
        // given
        UUID uuid = UUID.randomUUID();
        URL mock = mock(URL.class);
        given(mock.openStream()).willReturn(InputStream.nullInputStream());
        given(presignedGetObjectRequest.url()).willReturn(mock);
        given(presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(presignedGetObjectRequest);

        // when
        InputStream is = s3BinaryContentStorage.get(uuid);

        // then
        assertNotNull(is);
    }

    @DisplayName("파일을 다운로드 할 수 있도록 PresignerURL을 생성해 리다이렉션 합니다.")
    @Test
    void download() throws Exception {
        // givne
        UUID uuid = UUID.randomUUID();
        BinaryContentDto binaryContentDto = new BinaryContentDto(uuid, 1L, "filename", "image/jpeg");
        given(presignedGetObjectRequest.url()).willReturn(new URL("https://amazon.com/test-url"));
        given(presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(presignedGetObjectRequest);
        // when
        ResponseEntity response = s3BinaryContentStorage.download(binaryContentDto);

        // then
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
    }
}
