package com.codeit.discodeit8.exception.user;

import java.time.Instant;
import java.util.Map;

public class UserWrongPasswordException extends UserException {

  public UserWrongPasswordException(Map<String, Object> details) {
    super(Instant.now(), UserErrorCode.USER_WRONG_PASSWORD, details);
  }
}
