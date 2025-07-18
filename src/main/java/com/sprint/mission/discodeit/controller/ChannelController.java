package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.response.ResponseDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "type": "PUBLIC",
                      "name": "PublicChannel1",
                      "description": "Create public channel #1",
                      "id": "8fecadca-93a0-446e-a7cf-2638f293c93d",
                      "createdAt": "2025-07-14T07:54:52.545917Z",
                      "updatedAt": null
                  }
                  """)
          ))
  })
  @RequestMapping(path = "/api/channels/public", method = RequestMethod.POST)
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "Private Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "type": "PRIVATE",
                      "name": null,
                      "description": null,
                      "id": "a5a4026a-f112-4398-8f88-a178a41001ce",
                      "createdAt": "2025-07-14T07:58:15.710845Z",
                      "updatedAt": null
                  }
                  """)
          ))
  })
  @RequestMapping(path = "/api/channels/private", method = RequestMethod.POST)
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  Channel with id {channelId} not found
                  """)
          )),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  "Private channel cannot be updated
                  """)
          )),
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "type": "PUBLIC",
                      "name": "NewPublicChannel",
                      "description": "update public channel",
                      "id": "8fecadca-93a0-446e-a7cf-2638f293c93d",
                      "createdAt": "2025-07-14T07:54:52.545917Z",
                      "updatedAt": "2025-07-14T09:10:02.738375Z"
                  }
                  """)
          ))
  })
  @RequestMapping(path = "/api/channels/public/{channel-id}", method = RequestMethod.PATCH)
  public ResponseEntity<Channel> update(@PathVariable("channel-id") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }

  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          )),
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          ))
  })
  @RequestMapping(path = "/api/channels/{channel-id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable("channel-id") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          ))
  })
  @RequestMapping(path = "/api/users/{user-id}/channels", method = RequestMethod.GET)
  public ResponseEntity<List<ChannelDto>> findAll(@PathVariable("user-id") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
