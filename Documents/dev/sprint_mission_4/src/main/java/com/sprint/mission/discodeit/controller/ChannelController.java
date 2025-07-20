package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    //의존 관계 설정
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService, ChannelMapper channelMapper) {
        this.channelService = channelService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/public")
    public ResponseEntity<ChannelResponseDto> createPublicChannel(@RequestBody PublicChannelCreateDto dto) {
        ChannelResponseDto pubChannel = channelService.createPublicChannel(dto);
        return ResponseEntity.ok(pubChannel);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/private")
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(@RequestBody PrivateChannelCreateDto dto) {
        ChannelResponseDto priChannel = channelService.createPrivateChannel(dto);
        return ResponseEntity.ok(priChannel);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/public/{channel-id}")
    public ResponseEntity<ChannelResponseDto> updatePublicChannel(@PathVariable("channel-id") UUID channelId, @RequestBody ChannelUpdateDto dto) {
        ChannelResponseDto channel = channelService.update(dto);
        return ResponseEntity.ok(channel);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{channel-id}")
    public ResponseEntity deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/private/{channel-id}")
    public ResponseEntity deletePrivateChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user-id}")
    public ResponseEntity<List<ChannelResponseDto>> getAllPublicChannels(@PathVariable("user-id") UUID channelId) {
        List<ChannelResponseDto> channels = channelService.findAllByUserId(channelId);
        return ResponseEntity.ok(channels);
    }
}
