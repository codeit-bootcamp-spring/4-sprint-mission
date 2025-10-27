package com.sprint.mission.discodeit.exception.notification;


import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class NotFoundNotificationException extends NotificationException {
  public NotFoundNotificationException() {super(ErrorCode.NOTIFICATION_NOT_FOUND);}

  public static NotFoundNotificationException withId(UUID notificationId) {
    NotFoundNotificationException exception = new NotFoundNotificationException();
    exception.addDetail("notificationId", notificationId);
    return exception;
  }
}
