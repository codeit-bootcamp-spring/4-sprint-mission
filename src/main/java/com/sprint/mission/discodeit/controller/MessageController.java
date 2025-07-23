package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController {
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Operation(summary = "Message 생성", operationId = "create_2", responses = {
            @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel | Author with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation"))),
            @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity create(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest, @Parameter(description = "Message 첨부 파일들") @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        List<BinaryContentPostDto> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            return binaryContentMapper.ofBinaryContentPostDto(file);
                        })
                        .toList())
                .orElse(new ArrayList<>());
        MessageResponseDto response = messageService.createMessage(messageCreateRequest, attachmentRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Message 내용 수정", operationId = "update_2", responses = {
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Message with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{message-id}")
    public ResponseEntity update(@Parameter(description = "수정할 Message ID") @PathVariable("message-id") UUID messageId, @RequestBody MessageUpdateRequest messageUpdateRequest) {
        MessageResponseDto response = messageService.updateMessage(messageId, messageUpdateRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Message 삭제", operationId = "delete_1", responses = {
            @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Message with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{message-id}")
    public ResponseEntity delete(@Parameter(description = "삭제할 Message ID") @PathVariable("message-id") UUID messageId) {
        messageService.removeMessage(messageId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Channel의 Message 목록 조회", operationId = "findAllByChannelId", responses = {
            @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllByChannelId(@Parameter(description = "조회할 Channel ID") @RequestParam UUID channelId) {
        List<MessageResponseDto> responses = messageService.findallByChannelId(channelId);

        return ResponseEntity.ok(responses);
    }
}
