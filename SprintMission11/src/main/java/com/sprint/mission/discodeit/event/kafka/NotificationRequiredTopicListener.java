package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @CacheEvict(value = "notifications", allEntries = true)
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      User author = userRepository.findById(event.getAuthorId())
          .orElseThrow(UserNotFoundException::new);
      Channel channel = channelRepository.findById(event.getChannelId())
          .orElseThrow(ChannelNotFoundException::new);
      String title = author.getUsername() + "(#" + channel.getName() + ")";
      if (channel.getType() == ChannelType.PRIVATE) {
        System.out.println("This is Private Channel");
        title = author.getUsername();
      }

      List<User> users = readStatusRepository
          .findAllByChannelIdWithUser(channel.getId()).stream()
          .filter(ReadStatus::isNotificationEnabled)
          .map(ReadStatus::getUser)
          .filter(u -> !u.getId().equals(author.getId()))
          .toList();

      for (User user : users) {
        Notification notification = new Notification(user, title, event.getContent());
        notificationRepository.save(notification);
      }

      log.info("[Kafka] MessageCreatedEvent 알림 처리 완료, 대상 users={}", users.size());

    } catch (JsonProcessingException e) {
      log.error("[Kafka] MessageCreatedEvent JSON 처리 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @CacheEvict(value = "notifications", allEntries = true)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      User user = userRepository.findById(event.getUserId())
          .orElseThrow(() -> new RuntimeException("User not found"));

      String title = "권한이 변경되었습니다.";
      String content = event.getRole() + " -> " + event.getNewRole();
      Notification notification = new Notification(user, title, content);
      notificationRepository.save(notification);

      log.info("[Kafka] RoleUpdatedEvent 알림 처리 완료, user={}", user.getUsername());

    } catch (JsonProcessingException e) {
      log.error("[Kafka] RoleUpdatedEvent JSON 처리 실패", e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  @CacheEvict(value = "notifications", allEntries = true)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);

      List<User> admins = userRepository.findAllByRole(Role.ADMIN);
      String title = "S3 파일 업로드 실패";
      String content = "업로드 요청 실패 이벤트 발생";

      for (User admin : admins) {
        Notification notification = new Notification(admin, title, content);
        notificationRepository.save(notification);
      }

      log.info("[Kafka] S3UploadFailedEvent 알림 처리 완료, admins={}", admins.size());

    } catch (JsonProcessingException e) {
      log.error("[Kafka] S3UploadFailedEvent JSON 처리 실패", e);
    }
  }
}
