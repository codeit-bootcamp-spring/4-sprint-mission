package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(404, "NOTIFICATION-001", "NOT_FOUND"),
    NOT_AUTHENTICATED(401, "NOTIFICATION-002", "NOT_AUTHENTICATED"),
    NOT_AUTHORIZED(403, "NOTIFICATION-003", "NOT_AUTHORIZED");

    private final int status;
    private final String code;
    private final String message;
}
