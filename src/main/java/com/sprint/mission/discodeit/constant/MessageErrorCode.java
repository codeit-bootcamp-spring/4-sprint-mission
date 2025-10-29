package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageErrorCode implements ErrorCode {

    MESSAGE_NOT_FOUND(404, "MESSAGE_001", "MESSAGE_NOT_FOUND"),
    UPDATE_CONTENT_IS_NULL(400, "MESSAGE_002", "UPDATE_CONTENT_IS_NULL");

    private final int status;
    private final String code;
    private final String message;

}
