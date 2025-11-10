package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(@Payload MessageCreateRequest request){
    log.info("메시지 생성 요청: request={}", request);
    List<BinaryContentCreateRequest> attachmentRequests = new ArrayList<>();
    messageService.create(request, attachmentRequests);
    simpMessagingTemplate.convertAndSend("/sub/messages", request);  }
}
