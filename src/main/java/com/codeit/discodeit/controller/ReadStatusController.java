package com.codeit.discodeit.controller;

import com.codeit.discodeit.mapper.ReadStatusMapper;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusCreateRequest;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final MessageService messageService;
  private final UserService userService;
  private final ChannelService channelService;
  private final ReadStatusService readStatusService;
  private final ReadStatusMapper readStatusMapper;

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ReadStatus.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "이미 읽음 상태가 존재함",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found")
          )
      )
  })
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
    ReadStatus readStatus = readStatusService
        .findReadStatusByUserIdAndChannelId(readStatusCreateRequest.getUserId(), readStatusCreateRequest.getChannelId());

    ReadStatusDto readStatusDto = readStatusMapper.toReadStatusDto(readStatus);
    return ResponseEntity
        .status(HttpStatus.CREATED) // 201 Created
        .body(readStatusDto);
  }

  @Operation(
      summary = "User의 Message 읽음 상태 목록 조회",
      description = "세션에 저장된 로그인 사용자 정보를 기반으로 해당 사용자의 읽음 상태 목록을 반환합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "Message 읽음 상태 목록 조회 성공"
  )
  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {

    User user = userService.findUserByUserId(userId);

    List<ReadStatus> readStatusesList =
        readStatusService.findReadStatusesByUserId(user.getId());

    List<ReadStatusDto> readStatusDtoList = readStatusesList.stream()
        .map(readStatusMapper::toReadStatusDto)
        .toList();


    return ResponseEntity.ok(readStatusDtoList);
  }

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ReadStatus.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")
          )
      )
  })
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
      @Parameter(
          name = "readStatusId",
          description = "수정할 읽음 상태 ID",
          required = true
      )
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
  ) {

    ReadStatus updateReadStatus = readStatusService.updateReadStatusByReadStatusId(
        readStatusId, readStatusUpdateRequest);

    ReadStatusDto readStatusDto = readStatusMapper.toReadStatusDto(updateReadStatus);
    return ResponseEntity.ok(readStatusDto);
  }

}


