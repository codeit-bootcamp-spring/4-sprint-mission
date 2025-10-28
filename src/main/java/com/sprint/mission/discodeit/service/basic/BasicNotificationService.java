package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.notification.NotificationForbidden;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "notificationCache") // TODO: accessToken은 자주 바뀌니까 key로 두지 않는게 더 적절한가?
    public List<NotificationDto> getNotifications(String accessTokenValue) {
        // accessToken -> 유저 정보 추출 -> 해당 유저(receiver)의 알림 목록 조회 -> DTO 변환 후 반환
        log.debug("# 알림 조회 비즈니스 로직 시작, accessTokenValue: {}", accessTokenValue);

        if (!jwtTokenProvider.validateAccessToken(accessTokenValue)) {
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        UUID userId = jwtTokenProvider.getUserId(accessTokenValue);
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        List<Notification> notifications = notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver);


        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "notificationCache", allEntries = true)
    public void deleteNotification(UUID notificationId, String accessTokenValue) {
        // accessToken -> 유저 정보 추출 -> user 검증
        // 인증되지 않은 요청 -> 401 ErrorResponse
        // 인가되지 않은 요청 -> 403 ErrorResponse (요청자 본인의 알림만 삭제 가능)
        // 둘 다 아니면 -> notificationId로 해당 알람 삭제
        log.debug("# 알림 삭제 비즈니스 로직 시작, " +
                "notificationId={}, accessTokenValue: {}", notificationId, accessTokenValue);

        if (!jwtTokenProvider.validateAccessToken(accessTokenValue)) {
            throw new DiscodeitException(ErrorCode.INVALID_TOKEN);
        }

        UUID userId = jwtTokenProvider.getUserId(accessTokenValue);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        if (notification.getSender().getId().equals(userId)) {
            throw NotificationForbidden.withId(notificationId);
        }

        notificationRepository.deleteById(notificationId);
        log.info("# notificationRepository 에서 {} 삭제 성공", notificationId);
    }
}
