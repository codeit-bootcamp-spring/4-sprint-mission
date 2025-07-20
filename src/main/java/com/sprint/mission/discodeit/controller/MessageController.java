package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/*
 * 기존: /v1/messages, PathVariable: message-id
 * 요구사항: /api/messages, PathVariable: messageId
 * 요구사항에 맞춰 변경하였습니다.
 *
 * CommonResponse를 만들고 스웨거 문서에 반영도 했습니다.
 * 하지만 코드잇에서 주어진 API 스펙과 프론트엔드 코드와 맞지 않아 모두 제거했습니다.
 * 그래서 CommonResponse가 쓰인 곳은 없지만, CommonResponse를 비롯한 에러를 담은 응답 코드에 대한 피드백을 받으면 좋을 것 같아서 함께 올립니다.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(
            @RequestPart("messageCreateRequest")
                @Valid MessageCreateDto dto,
            @RequestPart(value = "attachments", required = false)
                List<MultipartFile> attachments
    ) {
        MessageResponseDto created = messageService.create(dto, attachments);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> getMessages(@RequestParam(name = "channelId") UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("messageId") UUID messageId,
                                                  @ModelAttribute MessageUpdateDto dto) {
        return ResponseEntity.ok(messageService.update(messageId, dto));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
