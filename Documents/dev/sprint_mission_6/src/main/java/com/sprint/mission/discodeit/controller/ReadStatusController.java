package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.ApiResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.exception.dto.ErrorResponse;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 API")
@RestController
@RequestMapping("/api/readStatuses")
public class ReadStatusController {
    //의존 관계 설정
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService, ReadStatusMapper readStatusMapper, MessageService messageService) {
        this.readStatusService = readStatusService;
    }

    @Operation(summary = "Message 읽음 상태 생성", description = "User가 특정 Channel 에서 마지막으로 메세지를 읽은 시각을 기록합니다.", operationId = "create_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "CHANNEL_OR_USER_NOT_FOUND",
                                              "message": "Channel | User with id {channelId | userId} not found"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "READ_STATUS_EXISTS",
                                              "message": "ReadStatus with userId {userId} and channelId {channelId} already exists"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "CREATED",
                                              "message": "Message 읽음 상태가 성공적으로 생성됨",
                                              "userId": "uuid...",
                                              "channelId": "uuid..."
                                              "readAt": "2024-07-10T12:34:56"                                             
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ReadStatusDto> createChannelReadStatus (@RequestBody ReadStatusCreateDto dto) {
        ReadStatusDto readStatus = readStatusService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
    }

    @Operation(summary = "Message 읽음 상태 수정", description = "기존 읽음 상태 정보를 수정합니다.", operationId = "update_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "message": "Message 읽음 상태가 성공적으로 수정됨",
                                                 "data": [
                                                   {
                                                     "userId": "uuid...",
                                                     "channelId": "uuid...",
                                                     "messageId": "uuid...",
                                                     "readAt": "2024-07-10T12:34:56"
                                                   }
                                                 ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "READSTATUS_NOT_FOUND",
                                              "message": "ReadStatus with id {readStatusId} not found"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateChannelReadStatus (@PathVariable("readStatusId") UUID readStatusId, @RequestBody ReadStatusUpdateDto dto) {
        ReadStatusDto readStatus = readStatusService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(readStatus);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회", description = "지정된 User ID에 해당하는 모든 읽음 상태 정보를 조회합니다.", operationId = "findAllByUserId")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "message": "Message 읽음 상태 목록 조회 성공",
                                                 "data": [
                                                   {
                                                     "userId": "uuid...",
                                                     "channelId": "uuid...",                                                     "authorId": "uuid...",
                                                     "readAt": "2024-07-10T14:52:00",
                                                     "createdAt": "2024-07-10T14:50:00",
                                                     "updatedAt": "2024-07-10T14:51:00"
                                                   }
                                                 ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> getUserReadStatus (@RequestParam UUID userId) {
        List<ReadStatusDto> readStatus = readStatusService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(readStatus);
    }
}
