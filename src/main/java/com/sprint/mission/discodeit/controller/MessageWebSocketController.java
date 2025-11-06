package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    public void sendMessage(MessageCreateRequest request) {
        String destination = "/sub/channels/" + request.channelId() + ".messages";
        messagingTemplate.convertAndSend(destination, request.content());
    }
}
