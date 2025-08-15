package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserStatusWithUserIdNotFoundException extends UserException {

  public UserStatusWithUserIdNotFoundException(
      UUID userId) {
    super(
        Instant.now(),
        ErrorCode.USERSTATUS_WITH_USERID_NOT_FOUND,
        Map.of("userId", userId));
  }

}
