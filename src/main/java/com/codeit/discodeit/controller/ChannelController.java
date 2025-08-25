package com.codeit.discodeit.controller;

import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.swagger.SwaggerChannelController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDto> createPublicChannel(
      @Valid @RequestBody CreatePublicChannelRequestDto createPublicChannelRequestDto) {
    log.info("[POST /api/channels/public] 요청 수신: {}", createPublicChannelRequestDto);
    ChannelDto channelDto = channelService.createPublicChannel(createPublicChannelRequestDto);
    log.info("[POST /api/channels/public] 생성 완료: channelId={}", channelDto.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
  }

  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @Valid @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
    log.info("[POST /api/channels/private] 요청 수신: participantIds={}", privateChannelCreateRequest.getParticipantIds());
    ChannelDto channelDto = channelService.createPrivateChannel(privateChannelCreateRequest.getParticipantIds());
    log.info("[POST /api/channels/private] 생성 완료: channelId={}", channelDto.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
    log.info("[DELETE /api/channels/{}] 요청 수신", channelId);
    channelService.deleteChannel(channelId);
    log.info("[DELETE /api/channels/{}] 삭제 완료", channelId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> updateChannel(
      @PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest updateRequest) {
    log.info("[PATCH /api/channels/{}] 요청 수신: {}", channelId, updateRequest);
    ChannelDto updatedChannelDto = channelService.updatePublicChannel(channelId, updateRequest);
    log.info("[PATCH /api/channels/{}] 수정 완료", channelId);
    return ResponseEntity.ok(updatedChannelDto);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> getAllChannelsByUserId(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channelDtoList = channelService.findChannelListByUserId(userId);
    return ResponseEntity.ok(channelDtoList);
  }
}