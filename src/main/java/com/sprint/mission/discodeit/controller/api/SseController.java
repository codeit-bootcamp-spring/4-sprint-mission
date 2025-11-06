package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {
    private static final long TIMEOUT = 60L * 60L * 1000L;  //연결 유지 시간: 1시간
    private final SseService sseService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @AuthenticationPrincipal DiscodeitUserDetails user,
            @RequestHeader (value = "Last-Event-ID", required = false)UUID lastEventId) {
        UUID receiverId = user.getUserDto().id();
        return sseService.connect(receiverId, lastEventId);
    }
}
