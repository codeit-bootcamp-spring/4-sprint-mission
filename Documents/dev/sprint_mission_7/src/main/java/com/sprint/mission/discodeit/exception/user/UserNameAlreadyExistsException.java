package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNameAlreadyExistsException extends UserException {
    public UserNameAlreadyExistsException(String username) {
        super(ErrorCode.DUPLICATE_USER);
        withUserName(username);
    }
}
