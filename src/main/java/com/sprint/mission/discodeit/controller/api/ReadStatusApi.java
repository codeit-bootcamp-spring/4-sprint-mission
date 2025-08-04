package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface ReadStatusApi {
    @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId", responses = {
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity findALlByUserId(@Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId);

    @Operation(summary = "Message 읽은 상태 생성", operationId = "carete_1", responses = {
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel | User with id not found"))),
            @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함", content = @Content(examples = @ExampleObject(value = "ReadStatus with userId and channelId already exists"))),
            @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ReadStatusDto.class))),
    })
    ResponseEntity createReadStatus(@RequestBody ReadStatusCreateRequest readStatusCreateRequest);

    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1", responses = {
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ReadStatusDto.class))),
            @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "ReadStatus with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity updateReadStatus(@Parameter(description = "수정할 읽음 상태 ID") @PathVariable("read-status-id") UUID readStatusId, @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest);
}
