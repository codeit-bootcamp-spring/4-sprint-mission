package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class InvalidUserArgumentException extends UserException {

  public InvalidUserArgumentException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.INVALID_USER_ARGUMENT, details);
  }
}
