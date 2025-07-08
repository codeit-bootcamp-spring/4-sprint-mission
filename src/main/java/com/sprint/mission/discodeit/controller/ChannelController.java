package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelService channelService;

    //공개 채널을 생성할 수 있다. Post
    @PostMapping
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelRequest publicChannelRequest) {
        ChannelDto createChannel = channelService.createPublic(publicChannelRequest);
        return ResponseEntity.ok().body(createChannel);
    }
    // 비공개 채널을 생성할 수 있다. Post
    @PostMapping
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelRequest privateChannelRequest) {
        ChannelDto createChannel = channelService.createPrivate(privateChannelRequest);
        return ResponseEntity.ok().body(createChannel);
    }
    // 공개 채널의 정보를 수정할 수 있다. Patch
    @PatchMapping({"/{channel-id}"})
    public ResponseEntity<ChannelDto> updatePublicChannel(@PathVariable("channel-id") UUID channelId,
                                                          @RequestBody PublicChannelRequest publicChannelRequest) {
        ChannelDto updatePublicChannel = channelService.updatePublicChannel(channelId, publicChannelRequest);
        return ResponseEntity.ok(updatePublicChannel);
    }

    // 채널을 삭제할 수 있다. Delete
    @DeleteMapping("/{channel-id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
    // 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다. Get
    @GetMapping("/{user-id}")
    public ResponseEntity<List<ChannelDto>> getChannel(@PathVariable("user-id") UUID userId) {
        List<ChannelDto> findAllChannelOfUser = channelService.findAllChannels(userId);
        return ResponseEntity.ok(findAllChannelOfUser);
    }

}
