package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPublicChannel(
            @RequestBody ChannelCreateDto channelCreateDto) {
        ChannelResponseDto channelResponseDto = channelService.createPublicChannel(channelCreateDto);
        return ResponseEntity.ok(channelResponseDto);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(
            @RequestBody List<UserResponseDto> userResponseDtos) {
        ChannelResponseDto channelResponseDto = channelService.createPrivateChannel(userResponseDtos);
        return ResponseEntity.ok(channelResponseDto);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ChannelResponseDto> updateChannel(
            @RequestBody ChannelUpdateDto channelUpdateDto) {
        ChannelResponseDto channelResponseDto = channelService.updateChannelName(channelUpdateDto);
        return ResponseEntity.ok(channelResponseDto);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@RequestParam UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponseDto>> getChannel(@RequestParam UUID userId) {
        List<ChannelResponseDto> channelResponseDtos = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channelResponseDtos);
    }
}
