package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class InvalidAuthArgumentException extends AuthException {

  public InvalidAuthArgumentException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.INVALID_AUTH_ARGUMENT, details);
  }
}
