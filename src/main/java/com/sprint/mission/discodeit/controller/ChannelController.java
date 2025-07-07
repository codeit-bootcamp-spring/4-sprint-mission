package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ChannelController {
    private ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/channels/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        System.out.println("######### createPublicChannel");
        System.out.println("# request = " + request);

        return ResponseEntity.ok(channelService.create(request));
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/channels/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        System.out.println("######### createPrivateChannel");
        System.out.println("# request = " + request);

        return ResponseEntity.ok(channelService.create(request));
    }

    // 공개 채널 정보 수정
    @RequestMapping(value = "/channels/public/{channel-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> updatePublicChannel(@PathVariable("channel-id") UUID channelId,
                                                          @RequestBody PublicChannelUpdateRequest request) {
        System.out.println("######### updatePublicChannel");
        System.out.println("# request = " + request);


        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    // 채널 삭제
    @RequestMapping(value = "/channels/{channel-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        System.out.println("######### deleteChannel");
        System.out.println("# channelId = " + channelId);

        channelService.delete(channelId);

        return ResponseEntity.ok().build();
    }

    // /
    @RequestMapping(value = "users/{user-id}/channels", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllChannelsByUserId(@PathVariable("user-id") UUID userId) {
        System.out.println("######### findAllChannelsByUserId");
        System.out.println("# userId = " + userId);

        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
