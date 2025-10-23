package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class AuthPasswordNotMatchException extends AuthException {
    public AuthPasswordNotMatchException(String email) {
        super(ErrorCode.PASSWORD_NOT_MATCH);
        withEmail(email);
    }
}
