package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.Role;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicNotificationService implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "notificationsByUser", key = "#receiverId",
            unless = "#result == null || #result.isEmpty()")
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("알림 목록 조회 시작: receiverId={}", receiverId);
        List<NotificationDto> result = notificationRepository
                .findAllByReceiver_IdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
        log.info("알림 목록 조회 완료: receiverId={}, count={}", receiverId, result.size());
        return result;
    }

    @Override
    @CacheEvict(cacheNames = "notificationsByUser", key = "#requesterId")
    public void deleteForUser(UUID notificationId, UUID requesterId) {
        log.debug("알림 삭제 시작: notificationId={}, requesterId={}", notificationId, requesterId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다: id=" + notificationId)); // 글로벌 핸들러에서 404 매핑

        UUID ownerId = notification.getReceiver().getId();
        if (!ownerId.equals(requesterId)) {
            throw new AccessDeniedException("본인 알림만 삭제할 수 있습니다."); // 403 매핑
        }

        notificationRepository.delete(notification);
        log.info("알림 삭제 완료: notificationId={}, requesterId={}", notificationId, requesterId);
    }

    @Override
    public void notifyFailure(String subject, String message) {
        // 1) 관리자 전체 조회
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        if (admins.isEmpty()) {
            log.warn("관리자(ADMIN) 계정을 찾을 수 없어 실패 알림을 전송하지 못했습니다. subject={}", subject);
            return;
        }

        // 2) 알림 생성
        List<Notification> notifications = admins.stream()
                .map(admin -> new Notification(admin, subject, message))
                .toList();

        // 3) 저장
        notificationRepository.saveAll(notifications);

        log.info("관리자 실패 알림 생성: admins={}, subject={}", notifications.size(), subject);
    }
}
