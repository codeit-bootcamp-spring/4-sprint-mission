package com.codeit.discodeit8.exception.binarycontentstorage;

import java.time.Instant;
import java.util.Map;

public class BinaryContentStorageFailReadFileException extends BinaryContentStorageException {

  public BinaryContentStorageFailReadFileException(Map<String, Object> details) {
    super(Instant.now(), BinaryContentStorageErrorCode.BINARY_CONTENT_STORAGE_FAIL_READ_FILE,
        details);
  }
}
