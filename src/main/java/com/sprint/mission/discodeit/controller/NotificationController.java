package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        log.info("알림 조회 요청");
        UUID currentUserId = getCurrentUserId();
        List<NotificationDto> notifications = notificationService.getNotifications(currentUserId);
        log.debug("알림 조회 응답: {}", notifications);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notifications);
    }

    @DeleteMapping(path = "{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationId") UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof DiscodeitUserDetails userDetails)) {
            throw new DiscodeitException(ErrorCode.INVALID_USER_DETAILS);
        }

        UserDto userDto = userDetails.getUserDto();
        log.debug("권한 변경 알람 생성 완료: id={}, newRole={}", userDto.id(), userDto.role().name());
        return userDto.id();
    }
}
