package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DuplicateEmailException extends UserException {

  public DuplicateEmailException(
      UUID userId) {
    super(
        Instant.now(),
        ErrorCode.DUPLICATE_EMAIL,
        Map.of("userId", userId));
  }

}
