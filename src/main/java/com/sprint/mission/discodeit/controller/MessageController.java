package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/messages")
@Tag(name = "Message", description = "Message API")
public class MessageController implements MessageApi {
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentService binaryContentService;
    private final PageResponseMapper pageResponseMapper;

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "messageCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity create(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                 @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        log.info("POST /api/messages 호출");
        List<UUID> attachmentRequests = Optional.ofNullable(attachments)
                .map(files -> files.stream()
                        .map(file -> {
                            BinaryContent binaryContent = binaryContentService.create(file);
                            return binaryContent.getId();
                        })
                        .toList())
                .orElse(new ArrayList<>());
        Message message = messageService.createMessage(messageCreateRequest, attachmentRequests);
        MessageDto response = messageMapper.toDto(message);
        log.debug("생성 응답 = {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{message-id}")
    public ResponseEntity update(@PathVariable("message-id") UUID messageId,
                                 @RequestBody MessageUpdateRequest messageUpdateRequest) {
        log.info("PATCH /api/messages/{message-id} 호출 id = {}", messageId);
        Message message = messageService.updateMessage(messageId, messageUpdateRequest);
        MessageDto response = messageMapper.toDto(message);
        log.debug("수정 호출 = {}", response);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{message-id}")
    public ResponseEntity delete(@PathVariable("message-id") UUID messageId) {
        log.info("DELETE /api/messages/{message-id} 호출 id = {}", messageId);
        messageService.removeMessage(messageId);
        log.debug("삭제 응답 status = {}", HttpStatus.NO_CONTENT);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllByChannelId(@RequestParam UUID channelId,
                                             @RequestParam(required = false) Instant cursor,
                                             @PageableDefault(
                                                     size = 50,
                                                     page = 0,
                                                     sort = "createdAt",
                                                     direction = Sort.Direction.DESC
                                             ) Pageable pageable) {
        Page<Message> messages = messageService.findAllByChannelId(channelId, cursor, pageable);
        Page<MessageDto> messageDtos = messages.map(messageMapper::toDto);
        PageResponse response = pageResponseMapper.fromPage(messageDtos);
        return ResponseEntity.ok(response);
    }
}
