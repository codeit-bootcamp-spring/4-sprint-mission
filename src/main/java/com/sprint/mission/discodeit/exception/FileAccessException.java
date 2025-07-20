package com.sprint.mission.discodeit.exception;

import lombok.Getter;

public class FileAccessException extends RuntimeException {

    @Getter
    private ErrorCode exceptionCode;

    public FileAccessException(ErrorCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
