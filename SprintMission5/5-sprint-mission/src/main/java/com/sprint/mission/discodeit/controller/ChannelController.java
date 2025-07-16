package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
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

  @Operation(summary = "공개 채널 생성", description = "누구나 접근 가능한 공개 채널을 생성할 때 사용하는 API")
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
    ChannelDto channelDto = channelService.createPublicChannel(publicChannelCreateRequest);
    return ResponseEntity.ok(channelDto);
  }

  @Operation(summary = "비공개 채널 생성", description = "참여자 목록을 기반으로 비공개 채널을 생성합니다.")
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest dtos) {
    ChannelDto channelDto = channelService.createPrivateChannel(dtos.participantIds());
    return ResponseEntity.ok(channelDto);
  }

  @Operation(summary = "채널 이름 수정", description = "공개 채널의 이름과 설명을 수정합니다.")
  @PutMapping("/{id}")
  public ResponseEntity<ChannelDto> updateChannel(
      @PathVariable UUID id,
      @RequestBody PublicChannelUpdateRequest dto) {
    if (!id.equals(dto.id())) {
      throw new IllegalArgumentException("경로의 ID와 요청 본문의 ID가 일치하지 않습니다.");
    }
    ChannelDto channelDto = channelService.updateChannelName(dto);
    return ResponseEntity.ok(channelDto);
  }

  @Operation(summary = "채널 삭제", description = "지정한 ID의 채널을 삭제합니다.")
  @DeleteMapping("/{channel-id}")
  public ResponseEntity<Void> deleteChannel(@PathVariable("channel-id") UUID channelId) {
    channelService.deleteChannel(channelId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "사용자의 채널 목록 조회", description = "특정 사용자가 속한 채널 목록을 반환합니다.")
  @GetMapping
  public ResponseEntity<List<ChannelDto>> getChannelByUserId(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channelDtos = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channelDtos);
  }
}
