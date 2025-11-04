package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Cacheable(value = "userNotificationsCache", key = "#userId")
  @Override
  @Transactional
  public List<NotificationDto> getNotificationsForUser(UUID userId) {

    log.debug("알림 조회 시작: userId={}", userId);

    List<NotificationDto> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(
            userId)
        .stream()
        .map(n -> new NotificationDto(
            n.getId(),
            n.getCreatedAt(),
            n.getUser().getId(),
            n.getTitle(),
            n.getContent()
        ))
        .toList();

    log.info("알림 조회 완료: userId={}, 알림 수={}", userId, notifications.size());

    return notifications;
  }

  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Override
  @Transactional
  public void delete(UUID notificationId, UUID userId) {

    // 알림이 없는 경우: 404 ErrorResponse
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new EntityNotFoundException("알림 존재하지 않음"));

    // 인가되지 않은 요청: 403 ErrorResponse
    if (!notification.getUser().getId().equals(userId)) {
      throw new NotificationAccessDeniedException();
    }

    log.debug("알림 삭제 시작: userId={}, notificationId={}", userId, notificationId);
    notificationRepository.delete(notification);
    log.info("알림 삭제 완료: userId={}, notificationId={}", userId, notificationId);

  }
}
