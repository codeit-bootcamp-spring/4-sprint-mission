package com.codeit.discodeit.controller;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.mapper.MessageMapper;
import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final MessageMapper messageMapper;
  private final UserService userService;
  private final ChannelService channelService;
  private final BinaryContentMapper binaryContentMapper;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Message가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Message.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(
                  value = "Channel | Author with id {channelId | authorId} not found"
              )
          )
      )
  })
  public ResponseEntity<MessageDto> createMessage(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException {

    User user = userService.findUserByUserId(messageCreateRequest.getAuthorId());
    Channel channel = channelService.findChannelByChannelId(messageCreateRequest.getChannelId());

    List<BinaryContent> binaryContents = null;
    List<byte[]> attachmentBytes = null;
    if (attachments != null && !attachments.isEmpty()) {
      binaryContents = attachments.stream()
          .map(attachment -> {
            try {
              return BinaryContentMapper.attachmentToBinaryContent(attachment);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          })
          .toList();

      attachmentBytes = attachments.stream().map(attachment -> {
        try {
          return attachment.getBytes();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }).toList();
    }

    Message message = messageService.createMessage(
        messageMapper.toMessage(messageCreateRequest.getContent(), user, channel, binaryContents),
        attachmentBytes);
    MessageDto messageDto = messageMapper.toMessageDto(message);
    return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
  }

  @GetMapping
  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(
      responseCode = "200",
      description = "Message 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = Message.class))
      )
  )
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable
  ) {
    PageResponse<Message> pageResponse = messageService.findMessagesPerPage(channelId, pageable);

    List<MessageDto> messageDtoList = pageResponse.getContent().stream()
        .map(messageMapper::toMessageDto)
        .toList();

    PageResponse<MessageDto> dtoPageResponse = new PageResponse<>(
        messageDtoList,
        pageResponse.getNumber(),
        pageResponse.getSize(),
        pageResponse.isHasNext(),
        pageResponse.getTotalElements()
    );

    return ResponseEntity.ok(dtoPageResponse);
  }

  @DeleteMapping("/{messageId}")
  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Message with id {messageId} not found")
          )
      )
  })
  public ResponseEntity<Void> deleteMessage(
      @PathVariable("messageId") UUID messageId
  ) {

    DeleteMessageRequestDto requestDto = new DeleteMessageRequestDto();
    requestDto.setMessageId(messageId);
    messageService.deleteMessage(requestDto);

    return ResponseEntity.noContent().build(); // HTTP 204
  }

  @PatchMapping("/{messageId}")
  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message가 성공적으로 수정됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Message.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject(value = "Message with id {messageId} not found")
          )
      )
  })
  public ResponseEntity<MessageDto> updateMessage(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest
  ) {

    Message message = messageService.updateMessage(messageId, messageUpdateRequest);
    MessageDto updatedMessageDto = messageMapper.toMessageDto(message);

    return ResponseEntity.ok(updatedMessageDto);
  }
}
