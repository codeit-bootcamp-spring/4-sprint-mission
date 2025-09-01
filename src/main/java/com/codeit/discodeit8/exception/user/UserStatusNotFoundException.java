package com.codeit.discodeit8.exception.user;

import java.time.Instant;
import java.util.Map;

public class UserStatusNotFoundException extends UserException {

  public UserStatusNotFoundException(Map<String, Object> details) {
    super(Instant.now(), UserErrorCode.USER_STATUS_NOT_FOUND, details);
  }
}
