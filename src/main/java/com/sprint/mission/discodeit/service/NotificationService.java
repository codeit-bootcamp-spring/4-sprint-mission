package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> getNotificationsForUser(UUID userId);

  void delete(UUID notificationId, UUID userId);

}
