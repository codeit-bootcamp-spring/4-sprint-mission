package com.sprint.mission.discodeit.event.kafka.event_listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
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
    public void on(MessageCreatedEvent event) throws JsonProcessingException {
        log.info("kafka 이벤트 호출 메시지 생성 알림");
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) throws JsonProcessingException {
        log.info("kafka 이벤트 호출 유저 상태 변경 알림");
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) throws JsonProcessingException {
        log.info("kafka 이벤트 호출 S3 파일 업로드 실패 알림");
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(UserLogInOutEvent event) throws JsonProcessingException {
        log.info("kafka UserLogInOut Event");
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.UserLogInOutEvent", payload);
    }
}

