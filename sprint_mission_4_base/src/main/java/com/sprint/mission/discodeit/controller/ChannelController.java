package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/channels")
public class ChannelController {

    private final ChannelService channelService;

    // [x] 공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        ChannelResponseDto responseDto = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // [x] 비공개 채널을 생성할 수 있다.
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        ChannelResponseDto responseDto = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // [x] 공개 채널의 정보를 수정할 수 있다.
    @RequestMapping(value = "/public/{channel-id}", method = RequestMethod.PUT)
    public ResponseEntity updatePublicChannel(@PathVariable("channel-id") String channelId, @RequestBody PublicChannelUpdateRequest request) {
        ChannelResponseDto responseDto = channelService.update(UUID.fromString(channelId), request);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // [x] 채널을 삭제할 수 있다.
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteChannel(@PathVariable("channelId") String channelId) {
        channelService.delete(UUID.fromString(channelId.toString()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // [x] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity getChannelsVisibleToUser(@PathVariable("userId") String userId) {
        List<ChannelResponseDto> channels = channelService.findAllByUserId(UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}

