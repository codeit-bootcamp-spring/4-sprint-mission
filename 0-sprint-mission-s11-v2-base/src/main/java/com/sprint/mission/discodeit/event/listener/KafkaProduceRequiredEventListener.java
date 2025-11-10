package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.event.S3UploadFailedEvent;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.MDC;
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

  @Async("taskExecutor")
  @TransactionalEventListener // 기본 phase=AFTER_COMMIT
  public void on(MessageCreatedEvent event) {
    // 채널 단위 순서 보장을 위해 channelId를 key로 사용
    String key = Optional.ofNullable(event.message().getChannel().getId())
        .map(Object::toString)
        .orElse(null);

    send("discodeit.MessageCreatedEvent", key, event);
  }

  @Async("taskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    // 동일 사용자에 대한 순서 보장을 위해 userId를 key로 사용
    String key = Optional.ofNullable(event.user().getId())
        .map(Object::toString)
        .orElse(null);

    send("discodeit.RoleUpdatedEvent", key, event);
  }

  @Async("taskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    String key = Optional.ofNullable(event.adminId())
        .map(Object::toString)
        .orElse(null);

    send("discodeit.S3UploadFailedEvent", key, event);
  }

  /**
   * 공통 발행 로직: JSON 직렬화 + 헤더(traceId, eventType) + 비동기 전송 결과 로깅
   */
  private void send(String topic, String key, Object payload) {
    try {
      String json = objectMapper.writeValueAsString(payload);

      String traceId = Optional.ofNullable(MDC.get("requestId")).orElse("N/A");
      ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, json);
      record.headers().add(new RecordHeader("x-trace-id", traceId.getBytes(StandardCharsets.UTF_8)));
      record.headers().add(new RecordHeader("x-event-type",
          payload.getClass().getSimpleName().getBytes(StandardCharsets.UTF_8)));

      kafkaTemplate.send(record).whenComplete((result, ex) -> {
        if (ex != null) {
          log.error("❌ Kafka 전송 실패 topic={}, key={}, error={}", topic, key, ex.getMessage(), ex);
        } else {
          log.debug("✅ Kafka 전송 성공 topic={}, key={}, partition={}, offset={}",
              topic, key,
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset());
        }
      });
    } catch (JsonProcessingException e) {
      log.error("❌ 이벤트 직렬화 실패 topic={}, error={}", topic, e.getMessage(), e);
    }
  }
}