package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<List<NotificationDto>> sendNotification(@AuthenticationPrincipal
  DiscodeitUserDetails userDetails) {
    UUID userId = userDetails.getUserDto().id();

    log.info("알림 조회 요청: userId={}", userId);
    List<NotificationDto> notificationDtos = notificationService.getNotificationsForUser(userId);
    log.debug("알림 조회 응답: {}", notificationDtos);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notificationDtos);
  }

  @DeleteMapping(path = "{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @PathVariable("notificationId") UUID notificationId
  ) {

    UUID userId = userDetails.getUserDto().id();

    log.info("알림 삭제 요청: userId={}", userId);
    notificationService.delete(notificationId, userId);
    log.debug("알림 삭제 응답: userId={}", userId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
