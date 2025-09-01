package com.codeit.discodeit8.exception.user;

import java.time.Instant;
import java.util.Map;

public class UserNameEmailDuplicateException extends UserException {

  public UserNameEmailDuplicateException(Map<String, Object> details) {
    super(Instant.now(), UserErrorCode.USER_NAME_EMAIL_DUPLICATE, details);
  }
}
