package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.ApiResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.exception.dto.ErrorResponse;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    //의존 관계 설정
    private final MessageService messageService;

    public MessageController(MessageService messageService, MessageMapper messageMapper, ChannelService channelService, ChannelMapper channelMapper) {
        this.messageService = messageService;
    }

    @Operation(summary = "Message 생성", description = "메세지를 생성하고, 첨부 파일도 함께 업로드할 수 있습니다.", operationId = "create_2")
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
                                              "message": "Channel | Author with id {channelId | authorId} not found"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                                "content": "내일 참석 멤버가이 어떻게 되나요",
                                                "channelId": "uuid...",
                                                "authorId": "uuid...",
                                                "attachmentId": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> createMessage(@RequestPart("messageCreateRequest") MessageCreateDto dto,
                                                            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        MessageResponseDto message = messageService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "Message 내용 수정", description = "메세지 ID를 기반으로 메세지 내용을 수정합니다.", operationId = "update_2")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                                     "messageId": "uuid...",
                                                     "newContent": "내일 오는 사람?"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "MESSAGE_NOT_FOUND",
                                              "message": "Message with id {messageId} not found"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("messageId") UUID messageId, @RequestBody MessageUpdateDto dto) {
        MessageResponseDto message = messageService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Operation(summary = "Message 삭제", description = "메세지 ID를 기반으로 메세지를 삭제합니다.", operationId = "delete_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "MESSAGE_NOT_FOUND",
                                              "message": "Message with id {messageId} not found"
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("{messageId}")
    public ResponseEntity<MessageResponseDto> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Channel의 Message 목록 조회", description = "해당 Channel ID에 해당하는 Message 목록을 조회합니다.", operationId = "findAllByChannelId")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    implementation = MessageResponseDto.class
                            ),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                                     "messageId": "uuid...",
                                                     "content": "내일 참석 멤버가이 어떻게 되나요",
                                                     "channelId": "uuid...",
                                                     "authorId": "uuid...",
                                                     "attachmentId": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> getMessagesByChannel(@PathVariable("channel-id") UUID channelId) {
        List<MessageResponseDto> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
