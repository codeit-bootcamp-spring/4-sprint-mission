package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class S3BinaryContentStorageTest {

  private final S3BinaryContentStorage storage = new S3BinaryContentStorage();

  @Test
  @DisplayName("S3 업로드 및 get 테스트")
  void uploadAndGetTest() throws Exception {
    UUID id = UUID.randomUUID();
    byte[] data = "Hello S3 Test".getBytes();

    // 업로드
    storage.put(id, data);

    // 다운로드
    InputStream in = storage.get(id);
    byte[] downloaded = in.readAllBytes();

    assertThat(downloaded).isEqualTo(data);
    System.out.println("Upload & Get Success: " + id);
  }

  @Test
  @DisplayName("S3 Presigned URL 다운로드 테스트")
  void presignedUrlDownloadTest() {
    UUID id = UUID.randomUUID();
    byte[] data = "Presigned URL Test".getBytes();

    // 업로드 먼저
    storage.put(id, data);

    // 메타 정보 생성
    BinaryContentDto meta = new BinaryContentDto(
        id,
        id + ".txt",
        (long) data.length,
        "text/plain"
    );

    // Presigned URL 생성
    String presignedUrl = storage.generatePresignedUrl(meta.id().toString(), meta.contentType());
    System.out.println("Presigned URL: " + presignedUrl);

    assertThat(presignedUrl).isNotBlank();
  }
}
