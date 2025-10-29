package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationDto> getNotifications(UUID receiverId);

    void deleteNotification(UUID notificationId);

    void createMessageNotification(MessageCreatedEvent event);

    void createRoleUpdatedNotification(RoleUpdatedEvent event);

    void createS3UploadFailedNotification(S3UploadFailedEvent event);
}
