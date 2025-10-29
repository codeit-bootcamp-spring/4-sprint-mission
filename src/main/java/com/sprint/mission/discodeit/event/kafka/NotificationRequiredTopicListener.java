package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreated(String message) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(message, MessageCreatedEvent.class);
            notificationService.createMessageNotification(event);
        } catch (Exception e) {
            log.error("MessageCreatedEvent 처리 실패: error={}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdated(String message) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(message, RoleUpdatedEvent.class);
            notificationService.createRoleUpdatedNotification(event);
        } catch (Exception e) {
            log.error("RoleUpdatedEvent 처리 실패: error={}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailed(String message) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(message, S3UploadFailedEvent.class);
            notificationService.createS3UploadFailedNotification(event);
        } catch (Exception e) {
            log.error("S3UploadFailed 이벤트 처리 실패: error={}", e.getMessage(), e);
        }
    }
}
