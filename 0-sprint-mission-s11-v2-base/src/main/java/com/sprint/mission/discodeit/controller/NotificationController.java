package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getMyNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails principal
  ) {
    UUID userId = principal.getUserDto().id();
    log.debug("알림 목록 조회 요청: userId={}", userId);

    List<NotificationDto> list = notificationService.findAllByUserId(userId);
    return ResponseEntity.ok(list);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> confirmNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails principal
  ) {
    UUID userId = principal.getUserDto().id();
    log.debug("알림 확인(삭제) 요청: userId={}, notificationId={}", userId, notificationId);

    notificationService.deleteByIdForUser(notificationId, userId);

    return ResponseEntity.noContent().build(); // 204
  }
}