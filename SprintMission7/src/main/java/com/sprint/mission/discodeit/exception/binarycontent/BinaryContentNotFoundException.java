package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
  }
}
