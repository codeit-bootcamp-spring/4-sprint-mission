package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class UserException  extends DiscodeitException {
    protected UserException(ErrorCode errorCode) {super(errorCode);}

    public UserException withUserId(UUID userId) {
        setDetail("userId", userId);
        return this;
    }

    public UserException withUserName(String username) {
        setDetail("username", username);
        return this;
    }

    public UserException withEmail(String email) {
        setDetail("email", email);
        return this;
    }
}
