package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserEmailDuplicateException extends UserException {

  public UserEmailDuplicateException(String email) {
    super(ErrorCode.DUPLICATE_EMAIL, (Throwable) Map.of("email", email));
  }
}
