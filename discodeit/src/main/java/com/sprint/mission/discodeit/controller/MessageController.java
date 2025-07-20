package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final MessageService messageService;

    //메시지 전송
    @Operation(summary = "메시지 생성", description = "메시지 본문과 파일 첨부를 포함하여 메시지를 전송합니다.", operationId = "create_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메시지 전송 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")))
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> sendMessage(
                                                        @Parameter(description = "Message 생성 정보")
                                                        @RequestPart("messageCreateRequest") MessageCreateRequest messageDto,
                                                        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        MessageResponse message = messageService.create(messageDto, attachments);

        return ResponseEntity.ok(message);
    }

    //특정 채널의 메시지 목록 조회
    @Operation(summary = "channel의 Message 목록 조회", operationId = "findAllByChannelId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공",
                    content = @Content(mediaType = "*/*", array = @ArraySchema(schema = @Schema(implementation = MessageResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(@Parameter(description = "조회할 Channel ID")
                                                                           @RequestParam("channelId") UUID channelId) {
        List<MessageResponse> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    //메시지 수정
    @Operation(summary = "Message 내용 수정", operationId = "update_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 수정 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "Message with id {messageId} not found"))
            )
    })
    @PatchMapping(value = "/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(@Parameter(description = "수정할 Message ID")
                                                         @PathVariable("messageId") UUID messageId,
                                                         @RequestBody MessageUpdateRequestDto updateDto) {
        MessageResponse update = messageService.update(messageId, updateDto);

        return ResponseEntity.ok(update);
    }

    //메시지 삭제
    @Operation(summary = "메시지 삭제", description = "특정 메시지를 삭제합니다.", operationId = "delete_1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "메시지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "메시지를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(value = "{\"message\": \"Message with id {messageId} not found\"}")
                    )
            )
    })
    @DeleteMapping(value = "/{messageId}")
    public ResponseEntity deleteMessage(
                                        @Parameter(description = "삭제할 Message ID")
                                        @PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}
