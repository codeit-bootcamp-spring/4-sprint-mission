package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserEmailAlreadyExistsException extends UserException {
    public UserEmailAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.USER_EMAIL_ALREADY_EXISTS, details);
    }
}
