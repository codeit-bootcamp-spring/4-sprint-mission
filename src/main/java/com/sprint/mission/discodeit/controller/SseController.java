package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.basic.EmitterRegistry;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//@RestController
//@RequiredArgsConstructor
//public class SseController {
//
//  // 연결 유지 시간
//  private static final long TIMEOUT = 60L * 60L * 1000L;
//  private final EmitterRegistry registry;
//  private final NotificationService notificationService;
//
//  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//  public SseEmitter connect(@RequestParam UUID userId) {
//
//    SseEmitter emitter = new SseEmitter(TIMEOUT);
//
//    registry.add(userId, emitter);
//
//    try {
//      emitter.send(
//          SseEmitter.event()
//              .name("connected")
//              .data(Map.of("userId", userId))
//      );
//    } catch (Exception e) {
//      emitter.completeWithError(e);
//    }
//    return emitter;
//  }
//
//}
@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;  // ✅ 기존에 만든 SseService 사용

  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    UUID userId = userDetails.getUserDto().id();

    log.info("SSE connection established for userId={}", userId);
    return sseService.connect(userId, UUID.randomUUID());
  }
}