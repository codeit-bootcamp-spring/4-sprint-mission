package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentSaveFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/*
 * Spring Event -> Kafka 메시지 발행을 담당하는 Publisher 역할
 *
 * - Event 객체를 JSON 으로 변환
 * - KafkaTemplate 으로 지정된 Topic 에 전송
 * - Consumer 애플리케이션이 Topic 구독 후 처리
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 메시지 저장 성공 시 kafka 발행
    public void on(MessageCreatedEvent event) {
        log.debug("### Kafka MessageCreatedEvent Listener 시작");
        sendEvent("discodeit.MessageCreatedEvent", event);

    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        log.debug("### Kafka RoleUpdatedEvent Listener 시작");
        sendEvent("discodeit.RoleUpdatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(BinaryContentSaveFailEvent event) {
        log.debug("### Kafka BinaryContentSaveFailEvent Listener 시작");
        sendEvent("discodeit.BinaryContentSaveFailEvent", event);
    }

    private void sendEvent(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.info("### Kafka 전송 성공: topic={}, payload={}", topic, payload);
        } catch (Exception e) {
            log.error("### Kafka 전송 실패: topic={}", topic, e);
        }
    }
}
