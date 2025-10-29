package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    public void create(User receiver, String title, String content);

    public List<NotificationDto> getNotifications(UUID userId);

    public void deleteNotification(UUID notificationId, UUID userId);
}
