package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class NotificationException extends BusinessException{
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
