package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageWebSocketController {
    // 첨부파일이 없는 단순 텍스트 메시지인 경우 STOMP를 통해 메시지를 전송할 수 있도록 컨트롤러 구현
    private final SimpMessagingTemplate messageTemplate;

    public MessageWebSocketController(SimpMessagingTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @MessageMapping("/messages") // WebSocketConfig에서 Destination을 지정했음
    public void sendMessage(@Payload MessageCreateRequest messageCreateRequest) {
        messageTemplate.convertAndSend("/sub/messages", messageCreateRequest);
    }
}