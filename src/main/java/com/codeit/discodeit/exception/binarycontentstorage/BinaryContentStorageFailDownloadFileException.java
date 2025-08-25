package com.codeit.discodeit.exception.binarycontentstorage;

import java.time.Instant;
import java.util.Map;

public class BinaryContentStorageFailDownloadFileException extends BinaryContentStorageException {
  public BinaryContentStorageFailDownloadFileException(Map<String, Object> details) {
    super(Instant.now(), BinaryContentStorageErrorCode.BINARY_CONTENT_STORAGE_FAIL_DOWNLOAD_FILE, details);
  }
}
