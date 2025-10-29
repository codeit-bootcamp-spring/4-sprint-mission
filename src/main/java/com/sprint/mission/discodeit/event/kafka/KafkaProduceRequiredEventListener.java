package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailureEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
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
        sendToKafka(event, String.format("discodeit.%s", event.getClass().getSimpleName()));
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        sendToKafka(event, String.format("discodeit.%s", event.getClass().getSimpleName()));
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(BinaryContentUploadFailureEvent event) {
        sendToKafka(event, String.format("discodeit.%s", event.getClass().getSimpleName()));
    }


    private <T> void sendToKafka(T event, String topic) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.debug("Published {} event {} to Kafka", topic, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to send event to Kafka", e);
            throw new RuntimeException(e);
        }
    }
}
