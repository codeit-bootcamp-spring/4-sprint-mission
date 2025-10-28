package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationDto> findAllByReceiverId(UUID receiverId);
    void deleteForUser(UUID notificationId, UUID requesterId);
    void notifyFailure(String subject, String message);
}
