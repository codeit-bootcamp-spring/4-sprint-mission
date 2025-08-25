package com.codeit.discodeit.exception.binarycontent;

import java.time.Instant;
import java.util.Map;

public class BinaryContentNotFoundException extends BinaryContentException {
  public BinaryContentNotFoundException(Map<String, Object> details) {
    super(Instant.now(), com.codeit.discodeit.exception.binarycontent.BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND, details);
  }
}
