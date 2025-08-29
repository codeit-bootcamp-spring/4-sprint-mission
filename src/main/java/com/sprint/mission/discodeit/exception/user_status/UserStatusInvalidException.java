package com.sprint.mission.discodeit.exception.user_status;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserStatusInvalidException extends UserStatusException {
    public UserStatusInvalidException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
