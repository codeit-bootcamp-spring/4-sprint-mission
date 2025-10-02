package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class UserException extends BusinessException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
