package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.config.AdminRegistry;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;
  private final NotificationService notificationService;
  private final AdminRegistry adminRegistry;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path root,
      NotificationService notificationService,
      AdminRegistry adminRegistry
  ) {
    this.root = root;
    this.notificationService = notificationService;
    this.adminRegistry = adminRegistry;
  }


  @PostConstruct
  public void init() {
    log.info("📂 LocalBinaryContentStorage 초기화: root-path={}", root.toAbsolutePath());
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
        log.info("📁 디렉터리 생성 완료: {}", root.toAbsolutePath());
      } catch (IOException e) {
        log.error("❌ 디렉터리 생성 실패: {}", root.toAbsolutePath(), e);
        throw new RuntimeException(e);
      }
    }
  }

  @Retryable(
      include = RuntimeException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 300)
  )
  public UUID put(UUID binaryContentId, byte[] bytes) {
    log.info("put() thread={}", Thread.currentThread().getName());

    /*
     double fail = Math.random();
     if (fail < 0.95) { log.warn("임의 실패 발생 (테스트용) — RuntimeException 던짐, fail={}", fail);
       throw new RuntimeException("Random test failure for retry simulation"); }*/

    try {
      log.info("파일 저장 지연 시작");
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while simulating delay", e);
    }

    Path filePath = resolvePath(binaryContentId);
    log.info("📦 파일 저장 시작: id={}, path={}", binaryContentId, filePath.toAbsolutePath());

    if (Files.exists(filePath)) {
      throw new IllegalArgumentException("File with key " + binaryContentId + " already exists");
    }
    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
      outputStream.write(bytes);
      log.info("✅ 파일 저장 완료: {} ({} bytes)", filePath.toAbsolutePath(), bytes.length);
    } catch (IOException e) {
      log.error("❌ 파일 저장 실패: {}", filePath.toAbsolutePath(), e);
      throw new RuntimeException(e);
    }
    return binaryContentId;
  }

  public InputStream get(UUID binaryContentId) {
    Path filePath = resolvePath(binaryContentId);
    log.debug("📥 파일 읽기 요청: id={}, path={}", binaryContentId, filePath.toAbsolutePath());

    if (Files.notExists(filePath)) {
      log.warn("⚠️ 요청한 파일이 존재하지 않음: {}", filePath.toAbsolutePath());
      throw new NoSuchElementException("File with key " + binaryContentId + " does not exist");
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      log.error("❌ 파일 읽기 실패: {}", filePath.toAbsolutePath(), e);
      throw new RuntimeException(e);
    }
  }

  private Path resolvePath(UUID key) {
    return root.resolve(key.toString());
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto metaData) {
    InputStream inputStream = get(metaData.id());
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + metaData.fileName() + "\"")
        .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
        .body(resource);
  }

  @Recover
  public UUID recoverAfterRetries(RuntimeException ex, UUID binaryContentId, byte[] bytes) {
    String requestId = MDC.get("requestId"); // 실패한 요청의 RequestId 추출
    log.error("⚠️ 파일 저장 재시도 실패(종료): id={}, requestId={}, cause={}",
        binaryContentId, requestId, ex.getMessage(), ex);

    String title = "로컬 파일 업로드 실패";
    String content = String.format(
        "RequestId: %s%nBinaryContentId: %s%nError: %s",
        requestId,
        binaryContentId,
        ex.getMessage()
    );

    UUID adminId = adminRegistry.getAdminId();
    notificationService.create(adminId, title, content);

    // 예외를 다시 던져서 상위 로직에도 실패를 알림
    throw ex;
  }
}
