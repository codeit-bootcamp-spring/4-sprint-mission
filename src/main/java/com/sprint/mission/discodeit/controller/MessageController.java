package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(request.toServiceRequest(attachments)));
    }

    @Override
    @PreAuthorize("hasRole('USER') and @messageService.isAuthor(authentication.principal.userResponse.id, #messageId) == true")
    @PatchMapping( "/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(@PathVariable("messageId") UUID messageId, @RequestBody MessageUpdateRequest request) {
        return ResponseEntity.ok().body(messageService.updateContent(request.toServiceRequest(messageId)));
    }

    @Override
    @PreAuthorize("hasRole('USER') and @messageService.isAuthor(authentication.principal.userResponse.id, #messageId) == true")
    @DeleteMapping( "/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    // API 스펙 요규사항에 따른 설계 더 restful 한 api 설계 가능
    @Override
    @GetMapping( params = "channelId")
    public ResponseEntity<PageResponse<MessageResponse>> findMessagesByChannelId(@RequestParam("channelId") UUID channelId,
                                                                                 @RequestParam(value = "cursor", required = false) Instant cursor,
                                                                                 @PageableDefault(
                                                                                 size = 50,
                                                                                 page = 0,
                                                                                 sort = "createdAt",
                                                                                 direction = Direction.DESC
                                                                         ) Pageable pageable) {
        return ResponseEntity.ok().body(messageService.findMessagesByChannelId(channelId, cursor, pageable));
    }
}
