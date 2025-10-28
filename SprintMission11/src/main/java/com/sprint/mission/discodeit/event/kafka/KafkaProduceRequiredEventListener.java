package com.sprint.mission.discodeit.event.kafka;

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

  // 메시지 생성 이벤트
  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      log.info("[Kafka] Sending MessageCreatedEvent to Kafka: {}", payload);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    } catch (Exception e) {
      log.error("[Kafka] Failed to publish MessageCreatedEvent", e);
    }
  }

  // 권한 변경 이벤트
  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("[Kafka] RoleUpdatedEvent sent: {}", payload);
    } catch (Exception e) {
      log.error("[Kafka] Failed to send RoleUpdatedEvent", e);
    }
  }

  // S3 업로드 실패 이벤트
  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
      log.warn("[Kafka] S3UploadFailedEvent sent: {}", payload);
    } catch (Exception e) {
      log.error("[Kafka] Failed to send S3UploadFailedEvent", e);
    }
  }
}
