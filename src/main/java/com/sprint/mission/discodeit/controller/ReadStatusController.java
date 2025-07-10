package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusDto.*;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "ReadStatus 관련 API")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // ============================== Post - ReadStatus 생성 ==============================
  @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ReadStatusResponse.class))
      ),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))
      ),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found"))
      ),
  })
  @PostMapping
  public ResponseEntity<ReadStatusResponse> createReadStatus(
      @RequestBody ReadStatusRequest readStatusRequest
  ) {
    ReadStatusResponse readStatusResponse = readStatusService.create(readStatusRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponse);
  }

  // ============================== PATCH - ReadStatus 수정 ==============================
  @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = ReadStatusUpdateResponse.class))
      ),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found"))
      )
  })
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> updateReadStatus(
      @Parameter(description = "수정할 읽음 상태 ID")
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatusResponse response = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(response);
  }

  // ============================== GET - User의 ReadStatus 목록 조회 ==============================
  @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
      content = @Content(mediaType = "*/*",
          array = @ArraySchema(schema = @Schema(implementation = ReadStatusResponse.class)))
  )
  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> getReadStatusByUserId(
      @Parameter(description = "조회할 User ID")
      @RequestParam("userId") UUID userId
  ) {
    List<ReadStatusResponse> readStatusResponseDtos = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatusResponseDtos);
  }
}
