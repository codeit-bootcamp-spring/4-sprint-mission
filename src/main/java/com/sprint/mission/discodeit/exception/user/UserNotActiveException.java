package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotActiveException extends UserException {

  public UserNotActiveException() {
    super(ErrorCode.USER_NOT_ACTIVE);
  }
}
