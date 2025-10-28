package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.basic.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {
  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-notification", concurrency = "3")
  public void onMessageCreated(String kafkaEvent, Acknowledgment ack) throws Exception {
    MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

    Message message = event.message();
    String notificationContents = message.getChannel().getName();
    if (notificationContents.isEmpty() || notificationContents == null){
      notificationContents = message.getAuthor().getUsername();
    }

    String title = "보낸 사람(채널): " + notificationContents;
    String content = "메시지 내용: " + message.getContent();

    Channel channel = message.getChannel();
    List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(channel.getId());
    notificationService.create(readStatuses, title, content);
    ack.acknowledge();
    log.debug("✅ Consumed MessageCreatedEvent channelId={}", channel.getId());
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-notification", concurrency = "3")
  public void onRoleUpdated(String kafkaEvent, Acknowledgment ack) throws Exception {
    RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

    String title = "권한이 변경되었습니다.";
    String content = event.oldRole() + "->" + event.newRole();

    notificationService.create(event.user().getId(), title, content);
    ack.acknowledge();
    log.debug("✅ Consumed RoleUpdatedEvent userId={}", event.user().getId());
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "discodeit-notification", concurrency = "3")
  public void onS3UploadFailedEvent(String kafkaEvent, Acknowledgment ack) throws Exception {
    S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
    notificationService.create(event.adminId(), event.title(), event.content());

    ack.acknowledge();
    log.debug("✅ Consumed S3UploadFailedEvent userId={}", event.adminId());
  }
}
