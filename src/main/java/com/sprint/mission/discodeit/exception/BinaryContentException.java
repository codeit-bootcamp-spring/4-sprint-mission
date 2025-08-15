package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class BinaryContentException extends DiscodeitException {

  public BinaryContentException(Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(errorCode.getMessage(), timestamp, errorCode, details);
  }

}
