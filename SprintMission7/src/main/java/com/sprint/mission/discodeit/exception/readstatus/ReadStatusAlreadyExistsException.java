package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  public ReadStatusAlreadyExistsException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.DUPLICATE_READ_STATUS, details);
  }
}
