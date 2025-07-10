package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel 관련 API")
public class ChannelController {

  private final ChannelService channelService;

  // ============================== POST - Public Channel 생성 ==============================
  @Operation(summary = "Public Channel 생성", operationId = "create_3")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
      content = @Content(mediaType = "*/*",
          schema = @Schema(implementation = ChannelResponse.class))
  )
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublicChannel(
      @Parameter(description = "Public Channel 생성 정보")
      @RequestBody PublicChannelCreateRequest publicCreateChannelRequestDto
  ) {
    ChannelResponse channelResponse = channelService.createPublic(
        publicCreateChannelRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
  }

  // ============================== POST - Private Channel 생성 ==============================
  @Operation(summary = "Private Channel 생성", operationId = "create_4")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
      content = @Content(mediaType = "*/*",
          schema = @Schema(implementation = ChannelResponse.class))
  )
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivateChannel(
      @Parameter(description = "Private Channel 생성 정보")
      @RequestBody PrivateChannelCreateRequest privateCreateChannelRequestDto
  ) {
    ChannelResponse channelResponse = channelService.createPrivate(
        privateCreateChannelRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
  }

  // ============================== Patch - (Public) Channel 수정 ==============================
  @Operation(summary = "Channel 정보 수정", operationId = "update_3")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ChannelResponse.class))
      ),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      ),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "Private channel cannot be updated"))
      )
  })
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> updatePublicChannel(
      @Parameter(description = "수정할 Channel ID")
      @PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
  ) {
    ChannelResponse channelResponse = channelService.update(channelId, publicChannelUpdateRequest);
    return ResponseEntity.ok(channelResponse);
  }

  // ============================== DELETE - Channel 삭제 ==============================
  @Operation(summary = "Channel 삭제", operationId = "delete_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "Channel with id {channelId} not found"))
      )
  })
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(
      @Parameter(description = "삭제할 Channel ID") @PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  // ============================== GET - User의 Channel 목록 조회 ==============================
  @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
          content = @Content(mediaType = "*/*",
              array = @ArraySchema(schema = @Schema(implementation = UserChannelResponse.class)))
      )
  })
  @GetMapping
  public ResponseEntity<List<UserChannelResponse>> getChannelsByUser(
      @Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId
  ) {
    List<UserChannelResponse> channelResponses = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channelResponses);
  }
}
