package com.sprint.mission.discodeit.stoarge.s3;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {

  @Mock
  S3Client s3Client;

  private S3BinaryContentStorage storage;

  @BeforeEach
  void setUp() {
    storage = spy(new S3BinaryContentStorage(
        "access", "secret", "ap-northeast-2", "test-bucket"
    ));

  }

  @Test
  @DisplayName("put 성공")
  public void put_success(){

    //getS3Client()만 mock S3Client 반환하도록 오버라이드
    doReturn(s3Client).when(storage).getS3Client();

    UUID id = UUID.randomUUID();
    byte[] data = "hello".getBytes();

    storage.put(id, data);

    // ArgumentCaptor로 PutObjectRequest 검증
    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

    verify(s3Client).putObject(requestCaptor.capture(),
         any(RequestBody.class));

    assertThat(requestCaptor.getValue().bucket()).isEqualTo("test-bucket");
    assertThat(requestCaptor.getValue().key()).isEqualTo(id.toString());
  }

  @Test
  @DisplayName("get 성공")
  public void get_success() throws IOException {
    //getS3Client()만 mock S3Client 반환하도록 오버라이드
    doReturn(s3Client).when(storage).getS3Client();

    //given
    UUID id = UUID.randomUUID();
    byte[] data = "hello".getBytes();

    // Mock ResponseInputStream 생성
    ResponseInputStream<GetObjectResponse> mockResponse =
        new ResponseInputStream<>(
            GetObjectResponse.builder().contentLength((long) data.length).build(),
            AbortableInputStream.create(new ByteArrayInputStream(data))
        );

    when(s3Client.getObject(any(GetObjectRequest.class)))
        .thenReturn(mockResponse);

    InputStream result = storage.get(id);

    assertThat(result.readAllBytes()).isEqualTo(data);
  }

  @Test
  @DisplayName("다운로드 성공")
  void download_success() throws IOException {
    // given
    UUID id = UUID.randomUUID();
    BinaryContentDto dto = new BinaryContentDto(id, "hello.png", 123L, "image/png");

    S3BinaryContentStorage realStorage = new S3BinaryContentStorage(
        "accessKey",
        "secretKey",
        "region",
        "test-bucket"
    );

    // when
    ResponseEntity<Void> response = realStorage.download(dto);

    // then
    assertThat(response.getStatusCodeValue()).isEqualTo(302);
    assertThat(response.getHeaders().getFirst("Location"))
        .startsWith("https://"); // 실제 Presigned URL 형식만 확인
  }

  @Test
  @DisplayName("S3Client 성공")
  void getS3Client_success(){
    //given
    String region = "ap-northeast-2";

    //when
    S3Client s3Client = storage.getS3Client();

    //then
    assertThat(s3Client).isNotNull();
    assertThat(s3Client.serviceClientConfiguration().region()).isEqualTo(Region.of(region));
  }

  @Test
  @DisplayName("generatePresignedUrl 성공")
  void  generatePresignedUrl_success(){
    //given
    String bucket = "test-bucket";

    String key = UUID.randomUUID().toString();
    String contentType = "image/png";

    //when
    String url = storage.generatePresignedUrl(key, contentType);

    //then
    assertThat(url).isNotNull();
    assertThat(url).contains(bucket);
    assertThat(url).contains(key);
    assertThat(url).startsWith("https://");
  }
}