package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Instant timestamp;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.timestamp = Instant.now();
        this.details = new HashMap<>();
    }


    public DiscodeitException setDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }
}
