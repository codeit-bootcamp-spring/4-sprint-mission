package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class EmailAlreadyExistsException extends UserException {
    public EmailAlreadyExistsException(String email) {
        super(ErrorCode.DUPLICATE_EMAIL);
        withEmail(email);
    }
}
