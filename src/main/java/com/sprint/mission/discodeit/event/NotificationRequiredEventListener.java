package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final BasicNotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;

  /**
   * 메시지 생성 시 알림 생성
   */
  @TransactionalEventListener
  @Async("taskExecutor")
  public void on(MessageCreatedEvent event) {
    Message message = event.getMessage();

    // 보낸 사람은 제외
    UUID senderId = message.getAuthor().getId();
    UUID channelId = message.getChannel().getId();

    // 해당 채널의 알림 활성화된 사용자 조회
    List<ReadStatus> activeReadStatuses =
        readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId);

    String title = String.format("%s (#%s)", message.getAuthor().getUsername(), message.getChannel().getName());
    String content = message.getContent();

    activeReadStatuses.stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(senderId)) // 보낸 사람 제외
        .forEach(user ->
            notificationService.createNotification(user.getId(), title, content)
        );

    log.info("MessageCreatedEvent -> {} users notified", activeReadStatuses.size());
  }

  /**
   * 권한 변경 시 알림 생성
   */
  @TransactionalEventListener
  @Async("taskExecutor")
  public void on(RoleUpdatedEvent event) {
    String title = "권한이 변경되었습니다.";
    String content = event.getOldRole().name() + " -> " + event.getNewRole().name();

    notificationService.createNotification(event.getUserId(), title, content);

    log.info("RoleUpdatedEvent -> notification sent to user {}", event.getUserId());
  }

}
