package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications(@AuthenticationPrincipal DiscodeitUserDetails user) {
        List<NotificationDto> notifications = notificationService.findAllByReceiverId(user.getUserDto().id());
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId,
                                                   @AuthenticationPrincipal DiscodeitUserDetails user) {
        notificationService.deleteByIdAndReceiverId(notificationId, user.getUserDto().id());
        return ResponseEntity.noContent().build();
    }
}
