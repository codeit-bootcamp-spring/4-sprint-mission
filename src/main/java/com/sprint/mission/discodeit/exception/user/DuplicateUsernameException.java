package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DuplicateUsernameException extends UserException {

  public DuplicateUsernameException(
      UUID userId) {
    super(
        Instant.now(),
        ErrorCode.DUPLICATE_USERNAME,
        Map.of("userId", userId));
  }
}
