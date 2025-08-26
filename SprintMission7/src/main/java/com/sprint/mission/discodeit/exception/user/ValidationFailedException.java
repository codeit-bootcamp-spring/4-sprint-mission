package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class ValidationFailedException extends UserException {
  public ValidationFailedException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.VALIDATION_FAILED, details);
  }
}
