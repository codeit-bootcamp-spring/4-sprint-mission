package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.config.CacheConfig.CHANNELS_BY_USER;
import static com.sprint.mission.discodeit.config.CacheConfig.NOTIFICATIONS_BY_USER;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.Collections;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final SseService sseService;

  private static final String SSE_NOTIFICATION_EVENT_NAME = "notifications.created";

  @Transactional
  public void create(UUID receiverId, String title, String content) {
    // 권한 변경시에 사용됨
    Notification notification = new Notification();
    notification.setReceiverId(receiverId);
    notification.setTitle(title);
    notification.setContent(content);

    Notification savedNotification = notificationRepository.save(notification);
    NotificationDto notificationDto = new NotificationDto(savedNotification);
    sseService.send(Collections.singleton(receiverId), SSE_NOTIFICATION_EVENT_NAME, notificationDto);

    log.debug("알림 생성: receiver={}, title={}", receiverId, title);
  }

  @Transactional
  public void create(List<ReadStatus> readStatuses, String title, String content) {
    // 채널에 메세지가 생성될 때 사용됨
    for (ReadStatus readStatus : readStatuses) {
      UUID receiverId = readStatus.getUser().getId();
      create(receiverId, title, content);
    }
  }

  @Cacheable(cacheNames = NOTIFICATIONS_BY_USER, key = "#userId")
  @Transactional(readOnly = true)
  public List<NotificationDto> findAllByUserId(UUID userId) {
    List<Notification> notifications = notificationRepository.findAllByReceiverId(userId);
    return notifications.stream()
        .map(n -> new NotificationDto(
            n.getId(),
            n.getCreatedAt(),
            n.getReceiverId(),
            n.getTitle(),
            n.getContent()
        ))
        .toList();
  }

  @CacheEvict(cacheNames = NOTIFICATIONS_BY_USER, key = "#userId")
  @PreAuthorize("isAuthenticated()") // or hasRole('USER')
  @Transactional
  public void deleteByIdForUser(UUID notificationId, UUID userId) {
    Notification n = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationNotFoundException::new); // 404

    if (!n.getReceiverId().equals(userId)) {
      throw new AccessDeniedException("본인의 알림만 삭제할 수 있습니다."); // 403
    }

    notificationRepository.delete(n);
    log.debug("🗑️ 알림 삭제 완료: id={}, user={}", notificationId, userId);
  }
}
