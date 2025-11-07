package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@Slf4j
public class SseController {
    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@AuthenticationPrincipal DiscodeitUserDetails user,
                              @RequestHeader(value = "Last-Event-ID", required = false) UUID lastEventId) {
        UUID receiverId = user.getUserDto().id();
        log.debug("# SseEmitter connect 요청, receiverId={}, lastEventId={}", receiverId, lastEventId);
        return sseService.connect(receiverId, lastEventId);
    }
}
