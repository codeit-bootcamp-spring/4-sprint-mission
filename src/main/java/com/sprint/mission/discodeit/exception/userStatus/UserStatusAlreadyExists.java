package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserStatusException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExists extends UserStatusException {

  public UserStatusAlreadyExists(
      UUID userId) {
    super(
        Instant.now(),
        ErrorCode.USERSTATUS_ALREADY_EXISTS,
        Map.of("userId", userId));
  }

}
