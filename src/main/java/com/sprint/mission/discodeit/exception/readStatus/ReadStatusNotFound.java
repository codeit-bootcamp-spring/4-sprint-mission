package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFound extends ReadStatusException {

  public ReadStatusNotFound(
      UUID readStatusId) {
    super(
        Instant.now(),
        ErrorCode.READSTATUS_NOT_FOUND,
        Map.of(
            "readStatusId", readStatusId
        ));
  }

}
