package com.codeit.discodeit.storage;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.exception.exception.BinaryContentNotFoundException;
import com.codeit.discodeit.exception.exception.BinaryContentStorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path rootPath;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPathStr) {
    this.rootPath = Paths.get(rootPathStr);
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(rootPath);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to initialize storage root path", e);
    }
  }

  private Path resolvePath(UUID binaryContentId) {
    return rootPath.resolve(binaryContentId.toString());
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    Path path = resolvePath(binaryContentId);
    try {
      Files.write(path, bytes);
      return binaryContentId;
    } catch (IOException e) {
      throw new BinaryContentStorageException("Failed to store binary content with ID: " + binaryContentId, e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    Path path = resolvePath(binaryContentId);
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      if (!Files.exists(path)) {
        throw new BinaryContentNotFoundException(binaryContentId);
      }
      throw new BinaryContentStorageException("Failed to read binary content with ID: " + binaryContentId, e);
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    try (InputStream inputStream = get(binaryContentDto.id())) {
      ByteArrayResource resource = new ByteArrayResource(inputStream.readAllBytes());

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
          .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
          .contentLength(binaryContentDto.size())
          .body(resource);
    } catch (IOException e) {
      throw new BinaryContentStorageException("Failed to create download response for ID: " + binaryContentDto.id(), e);
    }
  }
}