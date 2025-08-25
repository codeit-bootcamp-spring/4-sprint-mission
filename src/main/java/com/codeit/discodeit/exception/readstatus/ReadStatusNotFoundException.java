package com.codeit.discodeit.exception.readstatus;

import java.time.Instant;
import java.util.Map;

public class ReadStatusNotFoundException extends ReadStatusException {
  public ReadStatusNotFoundException(Map<String, Object> details) {
    super(Instant.now(), ReadStatusErrorCode.READ_STATUS_NOT_FOUND, details);
  }
}
