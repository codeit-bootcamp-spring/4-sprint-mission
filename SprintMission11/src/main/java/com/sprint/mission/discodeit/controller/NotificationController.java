package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    if (userDetails == null) {
      return ResponseEntity.status(401).build();
    }

    List<NotificationDto> notifications = notificationService.getNotifications(userDetails.getUserDto().id());
    return ResponseEntity.ok(notifications);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    if (userDetails == null) {
      return ResponseEntity.status(401).build();
    }

    NotificationDto notification = notificationService.getNotification(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.receiverId().equals(userDetails.getUserDto().id())) {
      throw new AccessDeniedException("본인의 알림만 삭제할 수 있습니다.");
    }

    notificationService.deleteNotification(notificationId);
    return ResponseEntity.noContent().build();
  }
}
