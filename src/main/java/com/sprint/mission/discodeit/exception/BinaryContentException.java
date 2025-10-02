package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ErrorCode;

public class BinaryContentException extends BusinessException {
    public BinaryContentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
