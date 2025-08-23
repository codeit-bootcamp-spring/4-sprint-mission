package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserNameAlreadyExistsException extends UserException {
    public UserNameAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.USER_NAME_ALREADY_EXISTS, details);
    }
}
