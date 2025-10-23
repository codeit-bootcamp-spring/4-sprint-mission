package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserAlreadyLoggedInException extends UserException {

  public UserAlreadyLoggedInException() {
    super(ErrorCode.USER_ALREADY_LOGGED_IN);
  }
}
