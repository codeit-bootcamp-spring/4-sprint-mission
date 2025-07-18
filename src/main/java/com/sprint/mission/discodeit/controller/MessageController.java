package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.response.ResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@Tag(name = "Message", description = "Message API")
public class MessageController {

  private final MessageService messageService;

  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          )),
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          ))
  })
  @RequestMapping(
      path = "/api/messages",
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          )),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          ))
  })
  @RequestMapping(path = "/api/messages/{message-id}", method = RequestMethod.PATCH)
  public ResponseEntity<Message> update(@PathVariable("message-id") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    Message updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class)
          )),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          ))
  })
  @RequestMapping(path = "/api/messages/{message-id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable("message-id") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  [
                      {
                          "content": "hi",
                          "channelId": "8fe2aae8-a48b-4003-b2ce-d624a58e9d2b",
                          "authorId": "d3f1ee9c-79a1-4094-b301-56ee9714a108",
                          "attachmentIds": [],
                          "id": "52116946-20b0-477e-8d12-6f606adacfe8",
                          "createdAt": "2025-07-15T02:57:55.273700Z",
                          "updatedAt": null
                      }
                  ]
                  """)
          ))
  })
  @RequestMapping(value = "/api/channels/{channel-id}/messages", method = RequestMethod.GET)
  public ResponseEntity<List<Message>> findAllByChannelId(
      @PathVariable("channel-id") UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}
