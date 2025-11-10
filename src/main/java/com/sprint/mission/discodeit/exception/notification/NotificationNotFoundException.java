package com.sprint.mission.discodeit.exception.notification;

import java.util.UUID;

public class NotificationNotFoundException extends RuntimeException{
  private NotificationNotFoundException(String message) {
    super(message);
  }

  public static NotificationNotFoundException withId(UUID id) {
    return new NotificationNotFoundException("Notification not found with id: " + id);
  }

}
