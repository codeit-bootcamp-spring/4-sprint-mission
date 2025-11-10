package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @AuthenticationPrincipal DiscodeitUserDetails user,
      @RequestHeader(name = "Last-Event-ID", required = false) String lastEventIdHeader
  ) {
    // 인증 안 된 경우 방어
    if (user == null) {
      log.warn("SSE connect called without authenticated user");
      throw new RuntimeException("Unauthenticated SSE connection");
    }

    UUID receiverId = user.getUserDto().id();

    UUID lastEventId = null;
    if (lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
      try {
        lastEventId = UUID.fromString(lastEventIdHeader);
      } catch (IllegalArgumentException e) {
        log.warn("Invalid Last-Event-ID header: {}", lastEventIdHeader);
      }
    }

    log.info("SSE connect start: receiverId={}, lastEventId={}", receiverId, lastEventId);

    SseEmitter emitter = sseService.connect(receiverId, lastEventId);

    log.info("SSE connect success: receiverId={}, lastEventId={}", receiverId, lastEventId);
    return emitter;
  }
}
