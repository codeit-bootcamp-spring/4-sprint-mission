package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;

    @RequestMapping(method = RequestMethod.POST, value = "/public")
    public ResponseEntity createPublicChannel(@RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        log.info("POST /api/channels/public 호출");
        Channel channel = channelService.createPublicChannel(publicChannelCreateRequest);
        ChannelDto response = channelMapper.toChannelDto(channel);
        log.debug("공개 채널 생성 응답 = {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/private")
    public ResponseEntity createPrivateChannel(@RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        log.info("POST /api/channels/private 호출");
        Channel channel = channelService.createPrivateChannel(privateChannelCreateRequest);
        ChannelDto response = channelMapper.toChannelDto(channel);
        log.debug("비공개 채널 생성 응답 = {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{channel-id}")
    public ResponseEntity updatePublicChannel(@PathVariable("channel-id") UUID channelId,
                                              @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
        log.info("PATCH /api/channels/{channel-id} 호출 id = {}", channelId);
        Channel channel = channelService.updateChannel(channelId, publicChannelUpdateRequest);
        ChannelDto response = channelMapper.toChannelDto(channel);
        log.debug("공개 채널 수정 응답 = {}", response);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{channel-id}")
    public ResponseEntity deleteChannel(@PathVariable("channel-id") UUID channelId) {
        log.info("DELETE /api/channels/{channel-id} 호출 id = {}", channelId);
        channelService.deleteChannel(channelId);
        log.debug("채널 삭제 응답 status = {}", HttpStatus.NO_CONTENT);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findChannelByUserId(@RequestParam("userId") UUID userId) {
        List<Channel> channels = channelService.findAllByUserId(userId);
        List<ChannelDto> responses = channels.stream().map(channelMapper::toChannelDto).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

}
