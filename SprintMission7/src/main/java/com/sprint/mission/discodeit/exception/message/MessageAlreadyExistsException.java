package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class MessageAlreadyExistsException extends MessageException {

  public MessageAlreadyExistsException(
      Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(Instant.now(), ErrorCode.DUPLICATE_MESSAGE, details);
  }
}
