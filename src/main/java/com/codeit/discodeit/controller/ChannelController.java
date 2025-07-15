package com.codeit.discodeit.controller;


import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping(value = "/public", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Public Channel 생성",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Public Channel이 성공적으로 생성됨",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Channel.class)
              )
          )
      }
  )
  public ResponseEntity<Channel> createPublicChannel(
      @RequestBody CreatePublicChannelRequestDto createPublicChannelRequestDto
  ) {
    Channel channel = channelService.createPublicChannel(createPublicChannelRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  } // 유저가 누구인지 알 수 없는데


  @PostMapping(value = "/private", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Private Channel 생성",
      responses = {
          @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Channel.class)
              )
          )
      })
  public ResponseEntity<Channel> createPrivateChannel(
      @RequestBody CreatePrivateChannelRequestDto createPrivateChannelRequestDto) {

    Channel channel = channelService.createPrivateChannel(createPrivateChannelRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  @Operation(
      summary = "Channel 삭제",
      parameters = {
          @Parameter(name = "channelId", description = "삭제할 Channel ID", required = true)
      },
      responses = {
          @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
          @ApiResponse(
              responseCode = "404",
              description = "Channel을 찾을 수 없음",
              content = @Content(
                  mediaType = "application/json",
                  examples = @ExampleObject(value = "Channel with id {channelId} not found")
              )
          )
      }
  )
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
    channelService.deleteChannel(channelId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Channel 정보 수정",
      description = "Public Channel의 이름, 설명 등을 수정합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
              content = @Content(schema = @Schema(implementation = Channel.class))),
          @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
              content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
          @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
              content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
      }
  )
  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> updateChannel(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest updateRequest
  ) {
    Channel updatedChannel = channelService.updatePublicChannel(channelId, updateRequest);
    return ResponseEntity.ok(updatedChannel);
  }


  @GetMapping
  @Operation(
      summary = "User가 참여 중인 Channel 목록 조회",
      description = "특정 userId가 참여한 채널들의 리스트를 반환합니다.",
      parameters = {
          @Parameter(
              name = "userId",
              description = "조회할 User ID",
              required = true,
              in = ParameterIn.QUERY,
              schema = @Schema(type = "string", format = "uuid")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Channel 목록 조회 성공",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))
              )
          )
      }
  )
  public ResponseEntity<List<ChannelDto>> findChannelByUser(@RequestParam UUID userId) {
    List<ChannelDto> channels = channelService.findChannelDtoListByUserId(userId);
    return ResponseEntity.ok(channels);
  }


}