package com.codeit.discodeit8.exception.readstatus;

import com.codeit.discodeit8.exception.global.DiscodeitException;
import com.codeit.discodeit8.exception.global.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class ReadStatusException extends DiscodeitException {

  public ReadStatusException(Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
