package com.codeit.discodeit.exception.message;

import com.codeit.discodeit.exception.global.DiscodeitException;
import com.codeit.discodeit.exception.global.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class MessageException extends DiscodeitException {
  public MessageException(Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
