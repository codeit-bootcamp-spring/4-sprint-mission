package com.codeit.discodeit8.exception.user;

import com.codeit.discodeit8.exception.global.ErrorCode;
import com.codeit.discodeit8.exception.global.DiscodeitException;
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
