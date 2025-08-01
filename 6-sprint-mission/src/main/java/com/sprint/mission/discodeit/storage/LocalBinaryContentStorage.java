package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
@ConditionalOnProperty(value = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
  private static final Logger log = LoggerFactory.getLogger(LocalBinaryContentStorage.class);

  @Value("${discodeit.storage.local.root-path}")
  private Path root;

  @PostConstruct
  private void init(){
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("파일 초기화 오류", e);
    }
  }

  private Path resolvePath(UUID uuid) {
    return root.resolve(uuid.toString());
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try {
      Path path = resolvePath(id);
      Files.write(path, bytes);
      return id;
    }catch (IOException e){
      throw new RuntimeException("파일 저장 실패", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id));
    }catch (IOException e){
      throw new RuntimeException("파일 불러오기 실패", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    log.info("[DOWNLOAD] 다운로드 요청 수신 - UUID: {}", binaryContentDto.id());
    try(InputStream inputStream = get(binaryContentDto.id())){
      Resource resource = new InputStreamResource(inputStream);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
          .header(HttpHeaders.CONTENT_TYPE, binaryContentDto.contentType())
          .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(binaryContentDto.size()))
          .body(resource);
    }catch (IOException e){
      throw new RuntimeException("다운로드 실패", e);
    }
  }
}
