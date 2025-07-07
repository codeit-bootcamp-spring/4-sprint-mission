package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/messages")
    public ResponseEntity<MessageResponseDto> createMessage(@ModelAttribute @Valid MessageCreateDto dto) {
        MessageResponseDto created = messageService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/channels/{channel-id}/messages")
    public ResponseEntity<List<MessageResponseDto>> getMessages(@PathVariable("channel-id") UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    /*
     * 메시지 ID로 단일 조회는 요구사항에 없었지만 추가해놓았습니다
     */
    @RequestMapping(method = RequestMethod.GET, value = "messages/{message-id}")
    public ResponseEntity<MessageResponseDto> getMessage(@PathVariable("message-id") UUID messageId) {
        return ResponseEntity.ok(messageService.find(messageId));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "messages/{message-id}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("message-id") UUID messageId,
                                                  @ModelAttribute @Valid MessageUpdateDto dto) {
        return ResponseEntity.ok(messageService.update(dto));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "messages/{message-id}")
    public ResponseEntity<MessageResponseDto> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
