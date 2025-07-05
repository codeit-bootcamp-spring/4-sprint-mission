package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/messages")
public class MessageController {
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponseDto> createMessage(@ModelAttribute MessageMultipartDto dto) {
        return ResponseEntity.ok(
                messageService.createMessage(messageMapper.messageMultipartDtoToMessageCreateDto(dto)));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<MessageResponseDto> updateMessage(
            @ModelAttribute MessageMultipartUpdateDto dto) {
        MessageUpdateDto messageUpdateDto =
                messageMapper.messageMultipartUpdateDtoToMessageUpdateDto(dto);
        return ResponseEntity.ok(messageService.updateMessage(messageUpdateDto));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@RequestParam UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>> getMessages(@RequestParam UUID channelId) {
        List<MessageResponseDto> messageResponseDtos = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messageResponseDtos);
    }
}
