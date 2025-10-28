package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentSaveFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        log.debug("### Kafka MessageCreatedEvent Notification 시작");
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            User sender = userRepository.findById(event.userId())
                    .orElseThrow(() -> UserNotFoundException.withId(event.userId()));
            Channel channel = channelRepository.findById(event.channelId())
                    .orElseThrow(() -> ChannelNotFoundException.withId(event.channelId()));

            List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(event.channelId());
            List<User> receivers = readStatuses.stream()
                    .map(ReadStatus::getUser)
                    .filter(user -> !user.getId().equals(event.userId()))
                    .toList();

            for (User receiver : receivers) {
                Notification notification = new Notification(receiver, sender, channel, event.content());
                notificationRepository.save(notification);
            }
            log.info("### Kafka MessageCreatedEvent Notification 성공");
        } catch (JsonProcessingException e) {
            log.error("### Kafka MessageCreatedEvent Notification 실패");
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        log.debug("### Kafka RoleUpdatedEvent Notification 시작");
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            User sender = userRepository.findByRole(Role.ADMIN);
            User receiver = userRepository.findById(event.userId())
                    .orElseThrow(() -> UserNotFoundException.withId(event.userId()));
            Notification notification = new Notification(receiver, sender, null, "권한이 변경되었습니다.");
            notificationRepository.save(notification);

            log.info("### Kafka RoleUpdatedEvent Notification 성공");
        } catch (JsonProcessingException e) {
            log.error("### Kafka RoleUpdatedEvent Notification 실패");
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.BinaryContentSaveFailEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        log.debug("### Kafka BinaryContentSaveFailEvent Notification 시작");
        try {
            BinaryContentSaveFailEvent event = objectMapper.readValue(kafkaEvent, BinaryContentSaveFailEvent.class);

            User admin = userRepository.findByRole(Role.ADMIN);
            User sender = userRepository.findById(event.getSenderId())
                    .orElseThrow(() -> UserNotFoundException.withId(event.getSenderId()));
            String content = String.format("""
                    RequestId: %s
                    BinaryContentId: %s
                    Error: %s
                    """, event.getRequestId(), event.getBinaryContentId(), event.getErrorMessage());

            Notification notification = new Notification(
                    admin,
                    sender,
                    null,
                    content
            );
            notificationRepository.save(notification);
            log.info("### Kafka BinaryContentSaveFailEvent Notification 성공");
        } catch (JsonProcessingException e) {
            log.error("### Kafka BinaryContentSaveFailEvent Notification 실패");
            throw new RuntimeException(e);
        }
    }
}
