package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;

    @RequestMapping(method = RequestMethod.POST, value = "/public")
    public ResponseEntity createPublicChannel(@RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = channelService.createPublicChannel(publicChannelCreateRequest);
        ChannelDto response = channelMapper.toChannelDto(channel);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/private")
    public ResponseEntity createPrivateChannel(@RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        Channel channel = channelService.createPrivateChannel(privateChannelCreateRequest);
        ChannelDto response = channelMapper.toChannelDto(channel);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{channel-id}")
    public ResponseEntity updatePublicChannel(@Parameter(description = "수정할 Channel ID") @PathVariable("channel-id") UUID channelId, @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel channel = channelService.updateChannel(channelId, publicChannelUpdateRequest);
        ChannelDto response = channelMapper.toChannelDto(channel);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{channel-id}")
    public ResponseEntity deletePublicChannel(@Parameter(description = "삭제할 Channel ID") @PathVariable("channel-id") UUID channelId) {
        channelService.deleteChannel(channelId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findChannelByUserId(@Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId) {
        List<Channel> channels = channelService.findAllByUserId(userId);
        List<ChannelDto> responses = channels.stream().map(channelMapper::toChannelDto).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
