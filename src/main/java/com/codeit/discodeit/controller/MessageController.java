package com.codeit.discodeit.controller;

import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.swagger.SwaggerMessageController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
@RequestMapping("/api/messages")
public class MessageController implements SwaggerMessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> createMessage(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException {
    log.info("[POST /api/messages] 메시지 생성 요청 수신: content={}, attachmentsCount={}",
        messageCreateRequest.getContent(),
        attachments != null ? attachments.size() : 0);

    MessageDto messageDto = messageService.createMessage(messageCreateRequest, attachments);
    log.info("[POST /api/messages] 메시지 생성 완료: messageId={}", messageDto.id());

    return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(
      @PathVariable("messageId") UUID messageId) {
    log.info("[DELETE /api/messages/{}] 삭제 요청 수신", messageId);

    messageService.deleteMessage(messageId);

    log.info("[DELETE /api/messages/{}] 삭제 완료", messageId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> updateMessage(
      @PathVariable("messageId") UUID messageId,
      @Valid @RequestBody MessageUpdateRequest messageUpdateRequest) {
    log.info("[PATCH /api/messages/{}] 수정 요청 수신: {}", messageId, messageUpdateRequest);

    MessageDto messageDto = messageService.updateMessage(messageId, messageUpdateRequest);

    log.info("[PATCH /api/messages/{}] 수정 완료", messageId);
    return ResponseEntity.ok(messageDto);
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    PageResponse<MessageDto> pageResponse = messageService.findMessagesPerPage(channelId, pageable);
    return ResponseEntity.ok(pageResponse);
  }
}
