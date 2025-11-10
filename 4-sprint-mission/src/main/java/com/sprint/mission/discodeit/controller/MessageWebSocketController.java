package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @MessageMapping("/messages")
    public void sendMessage(@Payload MessageCreateRequest messageCreateRequest) {
        Message message = messageService.createMessage(messageCreateRequest, null);
    }
}
