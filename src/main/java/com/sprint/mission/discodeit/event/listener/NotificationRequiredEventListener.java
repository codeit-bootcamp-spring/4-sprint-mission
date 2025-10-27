package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final NotificationRepository notificationRepository;
  private final ReadStatusRepository readStatusRepository;
  private static final String ROLE_UPDATE_TITLE = "권한이 변경되었습니다.";

  @Async("customTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {

    User author = event.author();
    Channel channel = event.channel();
    String title = author.getUsername() + " (#" + channel.getName() + ")";
    String content = event.content();

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_IdAndNotificationEnabled(
        channel.getId(), true
    );

    log.debug("[NotificationRequiredEventListener] 알림 수신 가능 사용자 수: {}", readStatuses.size());

    List<Notification> notifications = readStatuses.stream()
        .filter(readStatus -> !readStatus.getUser().equals(author))
        .map(readStatus -> new Notification(title, content, readStatus.getUser()))
        .toList();

    notificationRepository.saveAll(notifications);

    log.debug("[NotificationRequiredEventListener] 알림 {}개 생성 완료", notifications.size());
  }

  @Async("customTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {

    User user = event.user();
    Role oldRole = event.oldRole();
    Role newRole = event.newRole();

    String content = oldRole.name() + " -> " + newRole.name();

    Notification notification = new Notification(ROLE_UPDATE_TITLE, content, user);

    Notification savedNotification = notificationRepository.save(notification);

    log.debug("[NotificationRequiredEventListener] 권한 변경 알림 생성 완료- id: {}",
        savedNotification.getId());
  }
}