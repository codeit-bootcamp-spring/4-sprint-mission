package com.sprint.mission.discodeit.event.kafka.topic_listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final BinaryContentService binaryContentService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            log.info("kafka 이벤트 수신 메시지 생성 알림");
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                    MessageCreatedEvent.class);
            notificationService.create(event.message());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            log.info("kafka 이벤트 수신 유저 권한 수정 알림");
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
                    RoleUpdatedEvent.class);
            notificationService.create(event.user(), event.oldRole(), event.newRole());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            log.info("kafka 이벤트 수신 S3 업로드 실패 알림");
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
                    S3UploadFailedEvent.class);
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
            notificationService.create(event.exception());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

