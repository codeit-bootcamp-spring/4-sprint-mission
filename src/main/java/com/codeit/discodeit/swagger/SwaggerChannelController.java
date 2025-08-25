package com.codeit.discodeit.swagger;

import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.dto.channel_service_dto.PrivateChannelCreateRequest;
import com.codeit.discodeit.dto.channel_service_dto.PublicChannelUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.mapper.ChannelMapper;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface SwaggerChannelController {

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
  ResponseEntity<ChannelDto> createPublicChannel(
      @RequestBody CreatePublicChannelRequestDto createPublicChannelRequestDto);

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
  ResponseEntity<ChannelDto> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest);

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
  ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId);

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
  ResponseEntity<ChannelDto> updateChannel(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest updateRequest);

  @GetMapping
  ResponseEntity<List<ChannelDto>> getAllChannelsByUserId(@RequestParam("userId") UUID userId);

}
