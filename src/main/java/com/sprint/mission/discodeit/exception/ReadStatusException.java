package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class ReadStatusException extends DiscodeitException {

  public ReadStatusException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage(), timestamp, errorCode, details);
  }

}
