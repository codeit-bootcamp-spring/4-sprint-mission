package com.codeit.discodeit8.storage;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  UUID put(UUID binaryContentId, byte[] bytes);

  InputStream get(UUID binaryContentId);

  ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
