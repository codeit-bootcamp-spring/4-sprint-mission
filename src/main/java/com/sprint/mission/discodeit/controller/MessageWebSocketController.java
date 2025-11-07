package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageWebSocketController {
    private final MessageService messageService;

    @MessageMapping("/messages") // STOMP의 /pub/messages 요청 처리
    public void handleMessage(MessageCreateRequest request) {
        log.info("# 첨부 파일 없는 메시지 생성 요청: request={}", request);
        MessageDto createdMessage = messageService.create(request);
        log.debug("# 첨부 파일 없는 메시지 생성 응답: {}", createdMessage);
    }
}
