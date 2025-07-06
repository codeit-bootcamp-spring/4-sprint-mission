package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/messages")
public class MessageController {

    private final MessageService messageService;

    //메시지 전송
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageResponseDto> sendMessage(@ModelAttribute MessageCreateDto message) {
        MessageResponseDto response = messageService.create(message);
        return ResponseEntity.ok(response);
    }

    //특정 채널의 메시지 목록 조회
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<MessageResponseDto>> getMessagesByChannel(@PathVariable("channel-id") UUID channelId) {
        List<MessageResponseDto> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    //메시지 수정
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("message-id") UUID messageId, @ModelAttribute MessageUpdateDto updateDto) {
        MessageResponseDto update = messageService.update(messageId, updateDto);

        return ResponseEntity.ok(update);
    }

    //메시지 삭제
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}
