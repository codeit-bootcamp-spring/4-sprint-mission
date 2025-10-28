package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService {

  private final NotificationRepository notificationRepository;

  @Transactional
  public void createNotification(UUID receiverId, String title, String content) {
    Notification notification = new Notification(receiverId, title, content);
    notificationRepository.save(notification);
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "userNotifications", key = "#receiverId")
  public List<NotificationDto> getNotifications(DiscodeitUserDetails userDetails) {
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userDetails.getUserDto().id())
        .stream()
        .map(NotificationDto::from)
        .toList();
  }

  @Transactional
  @CacheEvict(value = "userNotifications", key = "#notificationId")
  public void deleteNotification(UUID notificationId, DiscodeitUserDetails userDetails) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.getReceiverId().equals(userDetails.getUserDto().id())) {
      throw new AccessDeniedException("자신의 알림만 삭제할 수 있습니다.");
    }

    notificationRepository.delete(notification);

  }
  @Transactional
  public void sendRoleUpdatedNotification(UUID receiverId, String newRole) {
    Notification notification = new Notification(
        receiverId,
        "권한 변경 알림",
        "당신의 권한이 " + newRole + "(으)로 변경되었습니다."
    );
    notificationRepository.save(notification);
  }

  public void sendAdminAlert(String operationName, String binaryContentId, Exception exception) {
    String requestId = MDC.get("RequestId"); // MDC에 저장된 RequestId 가져오기

    // 알림 내용 구성
    String message = String.format("""
                🚨 [비동기 작업 실패 알림]
                RequestId: %s
                Operation: %s
                BinaryContentId: %s
                Error: %s
                """,
        requestId != null ? requestId : "N/A",
        operationName,
        binaryContentId,
        exception.getMessage()
    );

    log.warn(message);
  }
}
