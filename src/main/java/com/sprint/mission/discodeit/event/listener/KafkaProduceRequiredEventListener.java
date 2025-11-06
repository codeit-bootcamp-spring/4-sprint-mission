package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
    try {
        log.info("🔥 Kafka 이벤트 수신됨: {}", event);
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
        log.info("✅ Kafka 메시지 전송 완료: {}", payload);
    } catch (JsonProcessingException e) {
        log.error("Kafka 메세지 변환 실패", e);
    }
}

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메세지 변환 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(S3UploadFailedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메세지 변환 실패", e);
        }
    }
}

