package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(404, "USER_001", "USER_NOT_FOUND"),
    EMAIL_DUPLICATED(400, "USER_002", "EMAIL_DUPLICATED"),
    USER_NAME_DUPLICATED(400, "USER_003", "USER_NAME_DUPLICATED");

    private final int status;
    private final String code;
    private final String message;
}
