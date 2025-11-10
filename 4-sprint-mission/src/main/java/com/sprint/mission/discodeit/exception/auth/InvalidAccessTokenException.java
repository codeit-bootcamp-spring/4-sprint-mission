package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidAccessTokenException extends AuthException {
    public InvalidAccessTokenException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
