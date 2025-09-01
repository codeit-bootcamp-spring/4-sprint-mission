package com.codeit.discodeit8.exception.binarycontent;

import com.codeit.discodeit8.exception.global.DiscodeitException;
import com.codeit.discodeit8.exception.global.ErrorCode;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class BinaryContentException extends DiscodeitException {

  public BinaryContentException(Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
