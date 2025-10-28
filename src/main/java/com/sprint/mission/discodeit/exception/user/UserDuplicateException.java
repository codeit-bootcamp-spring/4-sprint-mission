package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserDuplicateException extends UserException {

  public UserDuplicateException(String userName) {
    super(ErrorCode.DUPLICATE_USER, (Throwable) Map.of("userName", userName));
  }
}
