package com.codeit.discodeit8.stoarge.s3;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit8.storage.s3.S3BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.ResponseEntity;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3BinaryContentStorageTest {

  @Mock S3Client s3Client;
  @Mock S3Presigner presigner;
  @Mock PresignedGetObjectRequest presignedGet;

  @Captor ArgumentCaptor<PutObjectRequest> putReqCaptor;
  @Captor ArgumentCaptor<GetObjectPresignRequest> presignReqCaptor;

  S3BinaryContentStorage storage;

  @BeforeEach
  void setUp() {
    // 생성자에서 실제 클라이언트를 만들지만, 곧바로 모킹 객체로 교체한다.
    storage = new S3BinaryContentStorage(
        "",        // accessKey (빈 문자열 -> DefaultCredentialsProvider 경로)
        "",        // secretKey
        "ap-northeast-2",
        "test-bucket"
    );

    // private final 필드를 테스트에서 모킹으로 교체
    ReflectionTestUtils.setField(storage, "s3Client", s3Client);
    ReflectionTestUtils.setField(storage, "presigner", presigner);
  }

  @Test
  void put_uploadsToS3_andReturnsId() {
    // given
    UUID id = UUID.randomUUID();
    byte[] data = "hello".getBytes();

    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(PutObjectResponse.builder().eTag("etag-123").build());

    // when
    UUID returned = storage.put(id, data);

    // then
    assertThat(returned).isEqualTo(id);

    verify(s3Client).putObject(putReqCaptor.capture(), any(RequestBody.class));
    PutObjectRequest req = putReqCaptor.getValue();

    assertThat(req.bucket()).isEqualTo("test-bucket");
    assertThat(req.key()).isEqualTo("uploads/" + id); // prefix가 포함되어야 함
    assertThat(req.contentType()).isEqualTo("application/octet-stream");
    assertThat(req.contentLength()).isEqualTo(data.length);
  }

  @Test
  void download_redirectsWithPresignedUrl_andBuildsCorrectKey() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    var dto = mock(BinaryContentDto.class);
    when(dto.id()).thenReturn(id);
    when(dto.contentType()).thenReturn("image/jpeg");

    when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGet);
    when(presignedGet.url()).thenReturn(new URL("https://example.com/presigned-get"));

    // when
    ResponseEntity<?> resp = storage.download(dto);

    // then: 302 리다이렉트 + Location 헤더
    assertThat(resp.getStatusCode().value()).isEqualTo(302);
    assertThat(resp.getHeaders().getFirst("Location"))
        .isEqualTo("https://example.com/presigned-get");

    // presign 요청 내용 검증 (버킷/키/콘텐츠타입)
    verify(presigner).presignGetObject(presignReqCaptor.capture());
    GetObjectPresignRequest psReq = presignReqCaptor.getValue();
    assertThat(psReq.signatureDuration()).isEqualTo(Duration.ofMinutes(10));
    assertThat(psReq.getObjectRequest().bucket()).isEqualTo("test-bucket");
    assertThat(psReq.getObjectRequest().key()).isEqualTo("uploads/" + id);
    assertThat(psReq.getObjectRequest().responseContentType()).isEqualTo("image/jpeg");
  }

  @Test
  void download_usesDefaultContentType_whenNull() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    var dto = mock(BinaryContentDto.class);
    when(dto.id()).thenReturn(id);
    when(dto.contentType()).thenReturn(null); // contentType 비어 있음

    when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
        .thenReturn(presignedGet);
    when(presignedGet.url()).thenReturn(new URL("https://example.com/presigned-get-2"));

    // when
    ResponseEntity<?> resp = storage.download(dto);

    // then
    assertThat(resp.getStatusCode().value()).isEqualTo(302);

    // default content-type 검증
    verify(presigner).presignGetObject(presignReqCaptor.capture());
    String ct = presignReqCaptor.getValue().getObjectRequest().responseContentType();
    assertThat(ct).isEqualTo("application/octet-stream");
  }
}
