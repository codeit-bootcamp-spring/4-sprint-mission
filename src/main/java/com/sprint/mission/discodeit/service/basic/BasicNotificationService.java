package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.constant.NotificationErrorCode;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotificationException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    @Transactional
    @CacheEvict(value = "notifications", key = "#receiver.id")
    public void create(User receiver, String title, String content) {
        Notification notification = new Notification(receiver, title, content);
        notificationRepository.save(notification);
    }

    @Transactional
    @Cacheable(value = "notifications", key = "#receiverId")
    public List<NotificationDto> getNotifications(UUID receiverId) {
        List<Notification> notifications = notificationRepository.findAllByReceiverId(receiverId);

        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        if(!notification.getReceiverId().equals(userId)) {
            throw new NotificationException(NotificationErrorCode.NOT_AUTHORIZED);
        }
    }
}
