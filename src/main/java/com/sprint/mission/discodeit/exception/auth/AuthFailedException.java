package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class AuthFailedException extends AuthException {
    public AuthFailedException(Map<String, Object> details) {
        super(ErrorCode.AUTH_FAILED, details);
    }
}
