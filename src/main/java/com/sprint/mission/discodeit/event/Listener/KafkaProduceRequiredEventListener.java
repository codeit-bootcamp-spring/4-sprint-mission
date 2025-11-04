package com.sprint.mission.discodeit.event.Listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentRecoverEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(event);
    kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(event);
    kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);

  }

  @Async
  @EventListener
  public void on(BinaryContentRecoverEvent event) throws JsonProcessingException {
    String requestId = MDC.get("REQUEST_ID");
    String payload = objectMapper.writeValueAsString(event);

    ProducerRecord<String, String> record = new ProducerRecord<>(
        "discodeit.BinaryContentRecoverEvent",
        payload
    );
    record.headers().add("X-Request-ID", requestId.getBytes(StandardCharsets.UTF_8));

    kafkaTemplate.send(record);
  }
}

