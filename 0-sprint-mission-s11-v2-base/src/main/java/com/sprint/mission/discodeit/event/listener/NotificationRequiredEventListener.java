package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Setter
//@Component 비활성화를 위한 주석처리, KafkaProduceRequiredEventListener 로 대체
public class NotificationRequiredEventListener {
  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void on(MessageCreatedEvent event) {

    Message message = event.message();
    String notificationContents = message.getChannel().getName();
    if (notificationContents.isEmpty() || notificationContents == null){
      notificationContents = message.getAuthor().getUsername();
    }

    String title = "보낸 사람: " + notificationContents;
    String content = "메시지 내용: " + message.getContent();

    Channel channel = message.getChannel();
    List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(channel.getId());
    notificationService.create(readStatuses, title, content);
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void on(RoleUpdatedEvent event) {

    String title = "권한이 변경되었습니다.";
    String content = event.oldRole().toString() + "->" + event.newRole().toString();
    notificationService.create(event.user().getId(), title, content);
  }
}
