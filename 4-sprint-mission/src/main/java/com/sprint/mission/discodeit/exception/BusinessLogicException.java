package com.sprint.mission.discodeit.exception;

import lombok.Getter;

public class BusinessLogicException extends RuntimeException {
    @Getter
    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
