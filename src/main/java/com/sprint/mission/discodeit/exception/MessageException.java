package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class MessageException extends BusinessException {
    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
