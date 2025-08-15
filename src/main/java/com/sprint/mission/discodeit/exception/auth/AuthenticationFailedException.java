package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.AuthException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class AuthenticationFailedException extends AuthException {

  public AuthenticationFailedException() {
    super(
        Instant.now(),
        ErrorCode.AUTHENTICATION_FAILED,
        Map.of() // No specific details needed
    );
  }
}
