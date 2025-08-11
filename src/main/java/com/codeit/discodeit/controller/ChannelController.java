package com.codeit.discodeit.controller;


import com.codeit.discodeit.mapper.ChannelMapper;
import com.codeit.discodeit.dto.channel_service_dto.*;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
  private final MessageService messageService;
  private final ReadStatusService readStatusService;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

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
  public ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody CreatePublicChannelRequestDto createPublicChannelRequestDto
  ) {

    Channel channel = channelService.createPublicChannel(channelMapper.toPublicChannel(createPublicChannelRequestDto));
    ChannelDto channelDto = toChannelDto(channel);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
  }


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
  public ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {

    Channel channel = channelService.createPrivateChannel(channelMapper.toPrivateChannel(), privateChannelCreateRequest.getParticipantIds());
    ChannelDto channelDto = toChannelDto(channel);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelDto);
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
  public ResponseEntity<ChannelDto> updateChannel(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest updateRequest
  ) {
    Channel channel = channelService.updatePublicChannel(channelId, updateRequest);
    ChannelDto updatedChannelDto = toChannelDto(channel);
    return ResponseEntity.ok(updatedChannelDto);
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> getAllChannelsByUserId(@RequestParam("userId") UUID userId) {
    List<Channel> channelList = channelService.findChannelListByUserId(userId);
    List<ChannelDto> channelDtoList = channelList.stream().map(this::toChannelDto).toList();
    return ResponseEntity.ok(channelDtoList);
  }


  private ChannelDto toChannelDto(Channel channel) {
    Optional<Message> lastMessage = messageService.findLastMessageInChannel(channel.getId());
    List<ReadStatus> readStatuses = readStatusService.findReadStatusesByChannelId(channel);
    return channelMapper.toChannelDto(channel, lastMessage, readStatuses, userMapper);
  }

}