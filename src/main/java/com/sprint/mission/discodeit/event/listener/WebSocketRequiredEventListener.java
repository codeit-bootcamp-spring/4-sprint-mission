package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class WebSocketRequiredEventListener {
    // 메세지가 생성되면 해당 엔드포인트로 메세지를 보내는 컴포넌트
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketRequiredEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreateRequest event) {
        // MessageCreatedEvent를 통해 새로운 메세지 생성 이벤트 확인
        log.info("new message created, {}", event.content());
        // SimpleMessagingTemplate를 통해 적절한 엔드포인트로 메시지 전송하기
        messagingTemplate.convertAndSend("/channels.{channelId}.message", event.content());
    }
}
