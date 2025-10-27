package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class UserAuthException extends BusinessException {
    public UserAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
