package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Slf4j
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
                            schema = @Schema(implementation = MessageDto.class),
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
    public ResponseEntity<MessageDto> createMessage(@RequestPart("messageCreateRequest") @Valid MessageCreateDto dto,
                                                    @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        log.info("메세지 생성 요청 수신: message={}", dto.getContent());
        if (attachments != null && attachments.size() > 0) {
            log.debug("메세지에 파일 첨부: attachmentId={}", dto.getAttachmentId());
        }
        MessageDto message = messageService.create(dto);
        log.info("메세지 생성 성공: message={}", message);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "Message 내용 수정", description = "메세지 ID를 기반으로 메세지 내용을 수정합니다.", operationId = "update_2")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageDto.class),
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
    public ResponseEntity<MessageDto> updateMessage(@PathVariable("messageId") UUID messageId, @RequestBody MessageUpdateDto dto) {
        log.info("메세지 수정 요청 수신: messageId={}", messageId);
        MessageDto message = messageService.update(dto);
        log.info("메세지 수정 성공: messageId={}", messageId);
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
    public ResponseEntity<MessageDto> deleteMessage(@PathVariable("messageId") UUID messageId) {
        log.info("메세지 삭제 요청 수신: messageId={}", messageId);
        messageService.delete(messageId);
        log.info("메세지 삭제 성공: messageId={}", messageId);
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
                                    implementation = MessageDto.class
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
    public ResponseEntity<Page<MessageDto>> getMessagesByChannel(@RequestParam("channel-id") UUID channelId,

                                                                 Pageable pageable) {
        Page<MessageDto> messages = messageService.findAllByChannelId(channelId,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
