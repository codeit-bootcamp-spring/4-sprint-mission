package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/channels")
public class ChannelController {

    private final ChannelService channelService;

    //공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ChannelResponseDto> createPublicChannel (@ModelAttribute PublicChannelCreateDto dto) {
        ChannelResponseDto publicChannel = channelService.createPublicChannel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    //비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ChannelResponseDto> createPrivateChannel (@ModelAttribute PrivateChannelCreateDto dto) {
        ChannelResponseDto privateChannel = channelService.createPrivateChannel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }

    //특정 사용자가 볼 수 있는 모든 채널 목록 조회
    @RequestMapping(value = "/{user-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelResponseDto>> findAllChannels(@PathVariable("user-id") UUID userId) {
        List<ChannelResponseDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    //공개 채널의 정보 수정
    @RequestMapping(value = "/{channel-id}",method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ChannelResponseDto> updatePublicChannel(@PathVariable("channel-id") UUID channelId, @ModelAttribute ChannelUpdateDto dto) {
        ChannelUpdateDto channelUpdateDto = new ChannelUpdateDto(
                channelId,
                dto.getNewName(),
                dto.getNewDescription()
        );
        ChannelResponseDto update = channelService.update(channelUpdateDto);
        return ResponseEntity.ok(update);
    }

    //채널 삭제
    @RequestMapping(value = "/{channel-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}
