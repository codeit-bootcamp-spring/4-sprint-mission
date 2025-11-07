package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class NotificationForbidden extends NotificationException {
    public NotificationForbidden() {
        super(ErrorCode.NOTIFICATION_FORBIDDEN);
    }

    public static NotificationForbidden withId(UUID notificationId) {
        NotificationForbidden exception = new NotificationForbidden();
        exception.addDetail("notificationId", notificationId);
        return exception;
    }
}
