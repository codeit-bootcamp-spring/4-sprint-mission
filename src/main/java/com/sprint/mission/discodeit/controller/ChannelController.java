package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.channel_service_dto.*;
import com.sprint.mission.discodeit.dto.channel_service_dto.EnterChannelRequest;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/discodeit/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPublicChannel(@ModelAttribute CreatePublicChannelRequestDto createPublicChannelRequestDto,
                                                                   HttpSession session) {

        UserResponseDto hostUser = (UserResponseDto) session.getAttribute("loginUser");
        createPublicChannelRequestDto.setHostUserId(hostUser.getUserId());
        ChannelResponseDto channelResponseDto = channelService.createPublicChannel(createPublicChannelRequestDto);
        return ResponseEntity.ok(channelResponseDto); // 201 Created
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(@ModelAttribute CreatePrivateChannelRequestDto createPrivateChannelRequestDto, HttpSession session) {
        UserResponseDto hostUser = (UserResponseDto) session.getAttribute("loginUser");
        createPrivateChannelRequestDto.setHostUserId(hostUser.getUserId());
        ChannelResponseDto channelResponseDto = channelService.createPrivateChannel(createPrivateChannelRequestDto);
        //private 채널 정의 필요, 단순 1대1 채널인가? 아니면 단지 이름만 없는 채널인가?
        return ResponseEntity.ok(channelResponseDto); // 201 Created
    }

    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> getPublicChannels() {
        List<ChannelResponseDto> channels = channelService.findPublicChannels();
        return ResponseEntity.ok(channels);
    }

    @RequestMapping(value = "/private", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> getPrivateChannels(HttpSession session) {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        List<ChannelResponseDto> channels = channelService.findPrivateChannelsByUserId(loginUser);
        return ResponseEntity.ok(channels);
    }

    @RequestMapping(value = "/public", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelResponseDto> updatePublicChannel(@ModelAttribute ChannelNameUpdateRequestDto channelNameUpdateRequestDto, HttpSession session) {
        UserResponseDto hostUser = (UserResponseDto) session.getAttribute("loginUser");
        channelNameUpdateRequestDto.setUserId(hostUser.getUserId());
        ChannelResponseDto channelResponseDto = channelService.updateChannelName(channelNameUpdateRequestDto);
        return ResponseEntity.ok(channelResponseDto);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteChannel(@ModelAttribute DeleteChannelRequestDto deleteChannelRequestDto,
                                                             HttpSession session) {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        deleteChannelRequestDto.setUserId(loginUser.getUserId());
        channelService.deleteChannel(deleteChannelRequestDto);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> getChannel(@PathVariable("userName") String userName) {
        Optional<User> user = userRepository.findUserByUserName(userName);
        List<ChannelResponseDto> channels = channelService.findAllChannelByUserId(user.get().getId());
        return ResponseEntity.ok(channels);
    }

    @RequestMapping(value="enter-channel", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> enterChannel(@ModelAttribute EnterChannelRequest enterChannelRequest,
                                                           HttpSession session) throws IOException {
        // 채널 입장 시 기존에 있던 채널의 메시지 수신정보를 출력
        // 채널 퇴장 시 채널의 메시지 수신 정보를 업데이트
        // 유저와 메시지 채널 정보 모두 변경

        // 채널 입장 기능을 구현 like 로그인
        // 현재 입장하고 있는 채널을 session으로 보유
        // 유저가 채널을 만들거나 새로운 채널에 입장했을 때 readStatus 생성

        // 메세지 수신 정보는 채널을 퇴장할 때 수정

        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        ChannelResponseDto channelResponseDto = channelService.findChannelDtoByChannelName(enterChannelRequest.getChannelName());
        channelService.enterChannel(loginUser.getUserId(), channelResponseDto.getChannelId()); // 유저가 채널에 가입된 상태가 아니라면 예외를 발생시킴
        session.setAttribute("currentChannel", channelResponseDto);
        return ResponseEntity.ok(channelResponseDto); // 201 Created
    }
}
