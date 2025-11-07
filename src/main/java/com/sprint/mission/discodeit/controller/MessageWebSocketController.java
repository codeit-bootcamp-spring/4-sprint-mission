package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/messages")
    public void sendMessage(MessageCreateRequest request) {
        log.info("웹소켓 텍스트 메시지 수신: channelId={}, authorId={}", request.channelId(), request.authorId());

        // 첨부파일은 웹소켓 경로에서 허용하지 않으므로 항상 빈 리스트
        List<BinaryContentCreateRequest> noAttachments = List.of();

        MessageDto created = messageService.create(request, noAttachments);

        String destination = "/sub/channels/" + request.channelId();
        log.debug("브로드캐스트 destination={}, messageId={}", destination, created.id());
        messagingTemplate.convertAndSend(destination, created);
    }
}

