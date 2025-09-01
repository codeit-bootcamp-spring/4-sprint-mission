package com.codeit.discodeit8.exception.binarycontentstorage;

import java.time.Instant;
import java.util.Map;

public class BinaryContentStorageFailSaveFileException extends BinaryContentStorageException {

  public BinaryContentStorageFailSaveFileException(Map<String, Object> details) {
    super(Instant.now(), BinaryContentStorageErrorCode.BINARY_CONTENT_STORAGE_FAIL_SAVE_FILE,
        details);
  }
}
