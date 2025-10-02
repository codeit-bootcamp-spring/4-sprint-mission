package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class ChannelException extends BusinessException {
    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }
}
