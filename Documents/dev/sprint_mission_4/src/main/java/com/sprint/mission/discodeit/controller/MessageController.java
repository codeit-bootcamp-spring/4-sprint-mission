package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {
    //의존 관계 설정
    private final MessageService messageService;
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;

    public MessageController(MessageService messageService, MessageMapper messageMapper, ChannelService channelService, ChannelMapper channelMapper) {
        this.messageService = messageService;
        this.channelService = channelService;
        this.channelMapper = channelMapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponseDto> createMessage(@RequestBody MessageCreateDto dto) {
        MessageResponseDto message = messageService.create(dto);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{message-id}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("message-id") UUID messageId, @RequestBody MessageUpdateDto dto) {
        MessageResponseDto message = messageService.update(dto);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{message-id}")
    public ResponseEntity<MessageResponseDto> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/channel/{channel-id}")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByChannel(@PathVariable("channel-id") UUID channelId) {
        List<MessageResponseDto> messages = messageService.findAllByChannelId(channelId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
