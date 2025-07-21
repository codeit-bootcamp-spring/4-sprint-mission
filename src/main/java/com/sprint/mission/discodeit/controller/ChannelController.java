package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channel")
@Tag(name = "채널 (Channel)", description = "채널 정보 조회, 생성, 수정 및 삭제와 관련된 API입니다.")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "새로운 공개 채널을 생성합니다.")
  @PostMapping("/public")
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "새로운 비공개 채널을 생성합니다.")
  @PostMapping("/private")
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "특정 채널의 정보를 수정합니다. (공개 채널만 가능)")
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> update(
      @Parameter(description = "수정할 채널의 고유 ID")
      @PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }

  @Operation(summary = "특정 채널을 삭제합니다.")
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 채널의 고유 ID")
      @PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "특정 사용자가 참여 중인 채널 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<ChannelDto>> findByUserId(
      @Parameter(description = "채널 목록을 조회")
      @RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
  @Operation(summary = "특정 채널을 조회합니다.")
  @GetMapping("/{channelId}")
  public ResponseEntity<ChannelDto> findById(
      @Parameter(description = "조회할 채널의 고유 ID")
      @PathVariable("channelId") UUID channelId) {
    ChannelDto channel = channelService.find(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channel);
  }
}
