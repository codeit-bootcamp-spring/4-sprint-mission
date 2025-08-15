package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class UserStatusException extends DiscodeitException {

  public UserStatusException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage(), timestamp, errorCode, details);
  }

}
