package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel_service_dto.EnterChannelRequest;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/discodeit/receive-message")
public class ReceiveMessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;
    private final ReadStatusService readStatusService;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> createReadStatus(HttpSession session) throws IOException {
        // 원래는 채널을 만들거나 가입될 때 실행되어야 하지만 테스트를 위해 뺌
        // 나중엔 이 부분을 채널 가입 또는 생성 쪽에 합치거나 자동으로 이쪽 부분을 쓰게 하면 될 듯
        // readStatus는 원래 채널 가입 될 때 만들어져야 함
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        ChannelResponseDto currentChannel = (ChannelResponseDto) session.getAttribute("currentChannel");
        ReadStatusResponseDto readStatusResponseDto = readStatusService.createReadStatus(loginUser, currentChannel);
        return ResponseEntity.ok(readStatusResponseDto); // 201 Created
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(HttpSession session) throws IOException {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        ChannelResponseDto currentChannel = (ChannelResponseDto) session.getAttribute("currentChannel");
        ReadStatusResponseDto readStatusResponseDto = readStatusService.updateReadStatus(loginUser.getUserId(), currentChannel.getChannelId());
        return ResponseEntity.ok(readStatusResponseDto);
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponseDto>> findReadStatusByUser(HttpSession session) throws IOException {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        List<ReadStatusResponseDto> readStatusResponseDtoList = readStatusService.findReadStatusResponsDtoByUserId(loginUser.getUserId());
        return ResponseEntity.ok(readStatusResponseDtoList);
    }

    @RequestMapping(value = "channel", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponseDto>> findReadStatusByChannel(HttpSession session) throws IOException {
        ChannelResponseDto currentChannel = (ChannelResponseDto) session.getAttribute("currentChannel");
        List<ReadStatusResponseDto> readStatusResponseDtoList = readStatusService.findReadStatusResponseDtoByChannelId(currentChannel.getChannelId());
        return ResponseEntity.ok(readStatusResponseDtoList);
    }
}


