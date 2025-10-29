package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {
    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final NotificationService notificationService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        log.info("✅ Kafka 메시지 수신됨: {}", kafkaEvent);
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                    MessageCreatedEvent.class);
            List< ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.channelId());

            User author = userRepository.findById(event.authorId())
                    .orElseThrow(() -> new UserNotFoundException().withId(event.authorId()));
            Channel channel = channelRepository.findById(event.channelId())
                    .orElseThrow(() -> new ChannelNotFoundException().withId(event.channelId()));
            String title = String.format("%s (#%s)", author.getUsername(), channel.getName());

            for (ReadStatus readStatus : readStatuses) {
                UUID receiverId = readStatus.getUser().getId();
                if (receiverId.equals(author.getId())) continue;

                NotificationDto dto = new NotificationDto(
                        null,
                        Instant.now(),
                        receiverId,
                        title,
                        event.content()
                );
                notificationService.createNotification(dto);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
                    RoleUpdatedEvent.class);
            User receiver = userRepository.findById(event.receiverId())
                    .orElseThrow(() -> new UserNotFoundException().withId(event.receiverId()));

            NotificationDto dto = new NotificationDto(
                    null,
                    Instant.now(),
                    receiver.getId(),
                    "권한이 변경되었습니다",
                    String.format("%s → %s", event.previousRole(), event.newRole())
            );
            notificationService.createNotification(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
            try {
                S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
                        S3UploadFailedEvent.class);

                String content = String.format("""
                    S3 파일 업로드 실패
                    RequestId: %s
                    BinaryContentId: %s
                    Error: %s
                    """, event.requestId(), event.binaryContentId(), event.errorMessage());

                User admin = userRepository.findByEmail(("admin@gmail.com"))
                        .orElseThrow(()-> new UserNotFoundException());

                NotificationDto dto = new NotificationDto(
                        null,
                        Instant.now(),
                        admin.getId(),
                        "시스템 알림",
                        content
                );
                notificationService.createNotification(dto);
            }catch (JsonProcessingException e) {
                throw new RuntimeException(e);
        }
    }
}

