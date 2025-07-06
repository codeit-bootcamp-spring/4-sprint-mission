package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.form.MessageForm;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final BinaryContentMapper binaryContentMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDto> createMessage(@ModelAttribute MessageForm form) {
        List<BinaryContentCreateRequest> attachments = binaryContentMapper.addBinaryContentCreateRequest(form);

        MessageCreateRequest request = new MessageCreateRequest(
                form.content(),
                form.channelId(),
                form.authorId()
        );
        Message message = messageService.create(request,attachments);
        MessageDto messageDto = messageService.makeDto(message);
        return new ResponseEntity<>(messageDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{message-id}",method = RequestMethod.PATCH)
    public ResponseEntity<MessageDto> patchMessage(@PathVariable("message-id") UUID messageId, @RequestBody MessageUpdateRequest request) {
        Message message = messageService.update(messageId,request);
        MessageDto messageDto = messageService.makeDto(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{message-id}",method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable("message-id") UUID messageId) {
        messageService.delete(messageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/channel/{channel-id}",method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto>> getMessageByChannelId(@PathVariable("channel-id") UUID channelId) {
        List<Message> foundMessages = messageService.findAllByChannelId(channelId);
        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message message : foundMessages) {
            messageDtos.add(messageService.makeDto(message));
        }
        return new ResponseEntity<>(messageDtos, HttpStatus.OK);
    }


}
