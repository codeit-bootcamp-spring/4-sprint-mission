package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReadStatusErrorCode implements ErrorCode {

    READ_STATUS_NOT_FOUND(404, "READ_STATUS_001", "READ_STATUS_NOT_FOUND"),
    READ_STATUS_ALREADY_EXIST(400, "READ_STATUS_002", "READ_STATUS_ALREADY_EXIST");

    private final int status;
    private final String code;
    private final String message;
}
