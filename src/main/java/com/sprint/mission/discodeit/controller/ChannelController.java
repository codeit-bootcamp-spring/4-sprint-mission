package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.AllChannelByUserIdResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class ChannelController {
    private final ChannelService channelService;

    @PostConstruct
    public void init() {
        ChannelCreateDto pubCreate = new ChannelCreateDto(
                ChannelType.PUBLIC,
                "공지", "공지 채널입니다",
                null, null);
        channelService.createPublicChannel(pubCreate);
    }

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/channels")
    public ResponseEntity<ChannelResponseDto> createChannel(@RequestParam("channel-type") ChannelType type,
                                                            @RequestBody @Valid ChannelCreateDto dto) {
        ChannelResponseDto created = new ChannelResponseDto();
        switch (type) {
            case PUBLIC:
                created = channelService.createPublicChannel(dto);
            break;
            case PRIVATE:
                created = channelService.createPrivateChannel(dto);
                break;
            default:
                break;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{user-id}/channels")
    public ResponseEntity<AllChannelByUserIdResponseDto> getChannels(@PathVariable("user-id") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    /*
     * 채널 ID로 단일 조회는 요구사항에 없었지만 추가해놓았습니다.
     * userId의 경우, 헤더로부터 전달받습니다. (추후 JWT 와 같은 토큰과 같은 용도라고 보시면 좋을 것 같습니다)
     */
    @RequestMapping(method = RequestMethod.GET, value = "channels/{channel-id}")
    public ResponseEntity<ChannelResponseDto> getChannel(@PathVariable("channel-id") UUID channelId,
                                                         @RequestHeader("user-id") UUID userId) {
        return ResponseEntity.ok(channelService.find(channelId, userId));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "channels/{channel-id}")
    public ResponseEntity<ChannelResponseDto> updateChannel(@PathVariable("channel-id") UUID channelId,
                                          @RequestBody @Valid ChannelUpdateDto dto) {
        return ResponseEntity.ok(channelService.update(dto));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "channels/{channel-id}")
    public ResponseEntity<ChannelResponseDto> deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
