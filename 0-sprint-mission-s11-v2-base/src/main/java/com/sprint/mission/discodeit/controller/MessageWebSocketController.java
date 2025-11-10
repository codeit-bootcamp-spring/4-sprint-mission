package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
  private final MessageService messageService;


  @MessageMapping("/messages")
  public ResponseEntity<MessageDto> create(MessageCreateRequest request, Principal principal) {

    log.info("this is somp message controller, message: {}", request);
    MessageDto createdMessage = messageService.create(request, null);
    return ResponseEntity.ok(createdMessage);

  }

}
