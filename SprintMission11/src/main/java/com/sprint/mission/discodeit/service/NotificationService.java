package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationService {
  List<NotificationDto> getNotifications(UUID userId);
  Optional<NotificationDto> getNotification(UUID notificationId);
  void deleteNotification(UUID notificationId);
  void notifyFailure(String requestId, UUID binaryContentId, String errorMessage);
}
