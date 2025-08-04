package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageApi {
    @Operation(summary = "Message 생성", operationId = "create_2", responses = {
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel | Author with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation"))),
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = MessageDto.class))),
    })
    ResponseEntity create(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest, @Parameter(description = "Message 첨부 파일들") @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);

    @Operation(summary = "Message 내용 수정", operationId = "update_2", responses = {
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Message with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity update(@Parameter(description = "수정할 Message ID") @PathVariable("message-id") UUID messageId, @RequestBody MessageUpdateRequest messageUpdateRequest);

    @Operation(summary = "Message 삭제", operationId = "delete_1", responses = {
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Message with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity delete(@Parameter(description = "삭제할 Message ID") @PathVariable("message-id") UUID messageId);

    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId", responses = {
            @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PageResponse.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity findAllByChannelId(@Parameter(description = "조회할 Channel ID") @RequestParam UUID channelId, @Parameter(description = "페이징 커서 정보", schema = @Schema(format = "date-time")) @RequestParam Instant cursor, @Parameter(description = "페이징 정보") Pageable pageable);
}
