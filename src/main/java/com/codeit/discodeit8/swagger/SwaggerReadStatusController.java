package com.codeit.discodeit8.swagger;

import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusCreateRequest;
import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RequestMapping("/api/readStatuses")
public interface SwaggerReadStatusController {

  @Operation(summary = "Message 읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Object.class) // 실제 구현체에 맞게 바꾸세요
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
  ResponseEntity<ReadStatusDto> createReadStatus(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest);

  @Operation(
      summary = "User의 Message 읽음 상태 목록 조회",
      description = "세션에 저장된 로그인 사용자 정보를 기반으로 해당 사용자의 읽음 상태 목록을 반환합니다."
  )
  @ApiResponse(
      responseCode = "200",
      description = "Message 읽음 상태 목록 조회 성공"
  )
  ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId);

  @Operation(summary = "Message 읽음 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Object.class) // 실제 구현체에 맞게 바꾸세요
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
  ResponseEntity<ReadStatusDto> update(
      @Parameter(name = "readStatusId", description = "수정할 읽음 상태 ID", required = true)
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest);
}
