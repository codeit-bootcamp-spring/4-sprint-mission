package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class ReadStatusException extends BusinessException {
    public ReadStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
