package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketRequiredEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        log.debug("# 웹 소켓 브로드캐스트 시작");
        Message content = messageRepository.findById(event.messageId())
                .orElseThrow(() -> MessageNotFoundException.withId(event.messageId()));
        messagingTemplate.convertAndSend(
                "/sub/channels." + event.channelId() + ".messages",
                content
        );
    }
}
