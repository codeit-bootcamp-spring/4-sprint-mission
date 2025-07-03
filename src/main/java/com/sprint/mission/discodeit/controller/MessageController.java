package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/discodeit/messages")
public class MessageController {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageResponseDto> createMessage(@ModelAttribute MessageCreateRequestDto messageCreateRequestDto,
                                                            HttpSession session) throws IOException {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        messageCreateRequestDto.setUserResponseDto(loginUser);
        MessageResponseDto messageResponseDto = messageService.createMessage(messageCreateRequestDto);

        return ResponseEntity.ok(messageResponseDto); // 201 Created
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponseDto> updateMessage(@ModelAttribute MessageUpdateRequestDto messageUpdateRequestDto,
                                                            HttpSession session) throws IOException {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        messageUpdateRequestDto.setUserResponseDto(loginUser);
        MessageResponseDto messageResponseDto = messageService.updateMessage(messageUpdateRequestDto);

        return ResponseEntity.ok(messageResponseDto); // 201 Created
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>> getMessages() throws IOException {
        List<MessageResponseDto> messageResponseDtoList = messageService.findAllMessage();
        return ResponseEntity.ok(messageResponseDtoList);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponseDto>>getMessagesByUser(@PathVariable UUID channelId) throws IOException {
        ChannelResponseDto channelResponseDto = channelService.findChannelDtoByChannelId(channelId);
        List<MessageResponseDto> messageResponseDtoList = messageService.findMessagesByChannelId(channelResponseDto.getChannelId());
        return ResponseEntity.ok(messageResponseDtoList);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMessage(@ModelAttribute DeleteMessageRequestDto deleteMessageRequestDto, HttpSession session) throws IOException {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        deleteMessageRequestDto.setUserResponseDto(loginUser);
        messageService.deleteMessage(deleteMessageRequestDto);
        return ResponseEntity.noContent().build();
    }
}
