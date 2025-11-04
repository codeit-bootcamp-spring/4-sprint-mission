package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/messages")
  public void sendMessage(MessageCreateRequest message) {
    UUID channelId = message.channelId();
    messagingTemplate.convertAndSend("/sub/chat/room/" + channelId, message);
  }
}
