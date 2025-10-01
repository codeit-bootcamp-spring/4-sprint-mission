package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAuthException extends UserException {
    public UserAuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
