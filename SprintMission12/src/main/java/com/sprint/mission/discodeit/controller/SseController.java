package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.BasicSseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

  private final BasicSseService basicSseService;

  @GetMapping("/api/sse")
  public SseEmitter connect(@AuthenticationPrincipal DiscodeitUserDetails user,
      @RequestHeader(value = "Last-Event-ID", required = false) UUID lastEventId) {
    UUID receiverId = user.getUserDto().id();
    return basicSseService.connect(receiverId, lastEventId);
  }
}
