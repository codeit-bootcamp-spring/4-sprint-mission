package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestHeader("Authorization") String authorization) {
        log.debug("# 알림 목록 조회 시작");
        String accessTokenValue = getAccessTokenValue(authorization);
        List<NotificationDto> dtos = notificationService.getNotifications(accessTokenValue);
        log.info("# 알림 목록 조회 성공");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtos);

    }

    @DeleteMapping(path = "{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationId") UUID notificationId,
                                                   @RequestHeader("Authorization") String authorization) {
        log.debug("# 알림 확인 api 시작");
        String accessTokenValue = getAccessTokenValue(authorization);
        notificationService.deleteNotification(notificationId, accessTokenValue);
        log.info("# 알림 확인 api 성공");
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    private String getAccessTokenValue(String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            log.error("# Authorization header not found");
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        return authorization.replace("Bearer ", "");
    }
}
