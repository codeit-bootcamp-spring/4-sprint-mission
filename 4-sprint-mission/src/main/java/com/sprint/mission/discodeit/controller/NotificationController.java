package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;
    private final NotificationMapper notificationMapper;

    @GetMapping
    public ResponseEntity getNotifications(@RequestHeader("authorization") String accessToken) {
        log.info("GET /api/notification 호출");
        User user = authService.getUserByAccessToken(accessToken);
        List<Notification> notifications = notificationService.findAllByUserId(user.getId());
        List<NotificationDto> response = notifications.stream()
                .map(notification -> notificationMapper.toDto(notification))
                .collect(Collectors.toList());
        log.debug("알림 조회 응답 / 총 알림 수 = {}", response.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity deleteNotification(@RequestHeader("authorization") String accessToken,
                                             @PathVariable("notificationId") UUID notificationId) {
        log.info("DELETE /api/notification/{notificationId} 호출 id = {}", notificationId);
        User user = authService.getUserByAccessToken(accessToken);
        notificationService.delete(user, notificationId);
        log.debug("알림 삭제 응답 id = {}", notificationId);
        return ResponseEntity.noContent().build();
    }
}
