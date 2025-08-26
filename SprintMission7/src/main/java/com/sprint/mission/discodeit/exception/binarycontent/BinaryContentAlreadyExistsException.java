package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class BinaryContentAlreadyExistsException extends BinaryContentException {

  public BinaryContentAlreadyExistsException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.DUPLICATE_BINARY_CONTENT, details);
  }
}
