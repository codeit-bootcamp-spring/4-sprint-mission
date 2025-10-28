package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(
            @AuthenticationPrincipal(expression = "userDto.id") @NotNull UUID userId
    ) {
        log.debug("알림 조회 시작: userId={}", userId);
        List<NotificationDto> notifications = notificationService.findAllByReceiverId(userId);
        log.info("알림 조회 완료: userId={}, count={}", userId, notifications.size());
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> acknowledge(
            @PathVariable("notificationId") UUID notificationId,
            @AuthenticationPrincipal(expression = "userDto.id") @NotNull UUID userId
    ) {
        log.debug("알림 확인(삭제) 시작: userId={}, notificationId={}", userId, notificationId);
        notificationService.deleteForUser(notificationId, userId);
        log.info("알림 확인(삭제) 완료: userId={}, notificationId={}", userId, notificationId);
        return ResponseEntity.noContent().build();
    }
}
