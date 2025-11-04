package com.sprint.mission.discodeit.event.Listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentRecoverEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;

  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @CacheEvict(value = "userNotificationsCache", allEntries = true)
  public void onMessageCreatedEvent(String kafkaEvent) {

    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);
      log.info("MessageCreatedEvent 수신 성공 - 알림 생성 시작");

      Message message = event.message();
      Channel channel = message.getChannel();
      User user = message.getAuthor();

      List<ReadStatus> readStatusList = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(
          channel.getId());

      log.info("조회된 ReadStatus 개수: {}", readStatusList.size());

      List<Notification> notifications = readStatusList.stream()
          // 해당 메시지를 보낸 사람은 알림 대상에서 제외
          .filter(r -> !r.getUser().getId().equals(user.getId()))
          .map(r ->
              new Notification(
                  r.getUser(),
                  String.format("%s (#%s)", user.getUsername(), channel.getName()),
                  message.getContent()
              )
          ).toList();
      List<Notification> list = notificationRepository.saveAll(notifications);
      log.info("MessageCreatedEvent 수신 완료. list={}", list);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
          RoleUpdatedEvent.class);

      log.info("RoleUpdatedEvent 수신 성공 - 알림 생성 시작");

      User user = event.user();
      Notification notification = new Notification(
          user,
          "권한이 변경되었습니다.",
          String.format("%s -> %s", event.role(), user.getRole())
      );
      Notification savedNotification = notificationRepository.save(notification);
      log.info("RoleUpdatedEvent 수신 완료. savedNotification={}", savedNotification);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.BinaryContentRecoverEvent")
  public void onS3UploadFailedEvent(
      String kafkaEvent,
      @Header(name = "X-Request-ID", required = false) byte[] requestIdByte
  ) {

    try {
      BinaryContentRecoverEvent event = objectMapper.readValue(kafkaEvent,
          BinaryContentRecoverEvent.class);
      String requestId = (requestIdByte != null)
          ? new String(requestIdByte, StandardCharsets.UTF_8)
          : null;
      S3Exception s3Event = event.e();
      MDC.put("REQUEST_ID", requestId);
      log.info("BinaryContentRecoverEvent 수신 성공 - 알림 생성 시작");
      User adminUser = userRepository.findByUsername("admin")
          .orElseThrow(UserNotFoundException::new);

      Notification notification = new Notification(
          adminUser,
          "S3 파일 업로드 실패",
          String.format("RequestId: %s, "
                  + "BinaryContentId: %s, "
                  + "Error: %s",
              requestId,
              event.binaryContentId(),
              s3Event != null ? s3Event.getMessage() : "AWS S3 파일 업로드 오류"
          )
      );
      Notification savedNotification = notificationRepository.save(notification);
      log.info("BinaryContentRecoverEvent 수신 완료. savedNotification={}", savedNotification);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      MDC.clear();
    }
  }
}
