package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageResponseDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor // ?
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    // 특정 채널의 메시지 수신 정보를 생성할 수 있다. POST
    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(
            @RequestBody MessageCreateRequest messageCreateRequest) {
        MessageResponseDto createDto = messageService.createMessage(messageCreateRequest);
        return ResponseEntity.ok().body(createDto);
    }

    // 특정 채널의 메시지 수신 정보를 수정할 수 있다. PATCH
    // 특정 채널이니까 채널 id를 기준으로 찾아야할 것 같다..
    @PatchMapping("/{channel-id}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("channel-id") UUID channelId,
                                                            @RequestBody MessageUpdateRequest messageUpdateRequest) {
        MessageResponseDto updateDto = messageService.updateMessage(channelId, messageUpdateRequest);
        // messageUpdateDto 안에 들어있는 id는 messageId인가..
        return ResponseEntity.ok(updateDto);
    }

    // 특정 사용자의 메시지 수신 정보를 조회할 수 있다. GET
    @GetMapping("{/user-id}")
    public ResponseEntity searchMessage(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.ok(messageService.searchMessage(userId)); // 이래도 될까?
    }
}
