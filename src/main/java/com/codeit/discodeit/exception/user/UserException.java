package com.codeit.discodeit.exception.user;

import com.codeit.discodeit.exception.global.ErrorCode;
import com.codeit.discodeit.exception.global.DiscodeitException;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class UserException extends DiscodeitException {
  public UserException(Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
