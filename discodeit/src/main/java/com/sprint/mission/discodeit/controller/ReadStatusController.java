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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    //특정 채널의 수신 정보 생성
    @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "읽음 상태 생성 성공",
                    content = @Content(mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "읽음 상태가 이미 존재함",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "ReadStatus with userId {userId} and channelId {channelId} already exists"))
            ),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "Channel | User with id {channelId | userId} not found"))
            )
    })
    @PostMapping
    public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusRequest dto) {
        ReadStatusResponse created = readStatusService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //특정 채널의 수신 정보 수정
    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽음 상태 수정 성공",
                    content = @Content(mediaType = "*/*",
                            schema = @Schema(implementation = ReadStatusUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "ReadStatus with id {readStatusId} not found")))
    })
    @PatchMapping(value = "/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> update(@Parameter(description = "수정할 읽음 상태 ID")
                                                     @PathVariable("readStatusId") UUID readStatusId,
                                                     @RequestBody ReadStatusUpdateRequest dto) {
        ReadStatusResponse updated = readStatusService.update(readStatusId, dto);
        return ResponseEntity.ok(updated);
    }

    //특정 사용자의 메시지 수신 정보 전체 조회
    @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
                    content = @Content(mediaType = "*/*", array = @ArraySchema(schema = @Schema(implementation = ReadStatusResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ReadStatusResponse> statuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(statuses);
    }
}
