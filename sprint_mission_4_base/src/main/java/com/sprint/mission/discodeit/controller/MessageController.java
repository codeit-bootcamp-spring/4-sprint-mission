package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.MutilFileCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/messages")
public class MessageController {

    private final MessageService messageService;

    // [x] 메시지를 보낼 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity sendMessage(@ModelAttribute MessageCreateRequest messageCreateRequest,
                                      @ModelAttribute MutilFileCreateRequest mutilFileCreateRequest) {
        MessageResponseDto responseDto = messageService.create(messageCreateRequest, mutilFileCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // [x] 메시지를 수정할 수 있다.
    @RequestMapping(value = "/{message-id}", method = RequestMethod.PUT)
    public ResponseEntity updateMessage(@PathVariable("message-id") String messageId,
                                        @RequestBody MessageUpdateRequest request) {

        MessageResponseDto responseDto = messageService.update(UUID.fromString(messageId), request);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // [x] 메시지를 삭제할 수 있다.
    @RequestMapping(value = "/{message-id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMessage(@PathVariable("message-id") String messageId) {

        messageService.delete(UUID.fromString(messageId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // [x] 특정 채널의 메시지 목록을 조회할 수 있다.
    @RequestMapping(value = "/channel/{channel-id}", method = RequestMethod.GET)
    public ResponseEntity getMessagesByChannel(@PathVariable("channel-id") String channelId) {

        List<MessageResponseDto> messages = messageService.findAllByChannelId(UUID.fromString(channelId));
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}


