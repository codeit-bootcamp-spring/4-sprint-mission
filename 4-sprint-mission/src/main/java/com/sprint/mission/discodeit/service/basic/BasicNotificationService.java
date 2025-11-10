package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.SseNotificationEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public List<Notification> create(Message message) {
        log.info("메시지 생성 알림 생성 호출");
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel_Id(message.getChannelId());
        List<Notification> notifications = readStatuses.stream()
                .filter(readStatus ->
                        !readStatus.getUserId().equals(message.getAuthorId()) &&
                                readStatus.isNotificationEnabled()
                )
                .map(readStatus -> {
                    Notification notification = new Notification(
                            readStatus.getUser(),
                            String.format("%s (#%s)",
                                    message.getAuthor().getUsername(),
                                    message.getChannel().getName()),
                            message.getContent());
                    notificationRepository.save(notification);
                    return notification;
                })
                .map(notification -> {
                    cacheManager.getCache("notifications").evict(notification.getReceiver().getId());
                    return notification;
                })
                .collect(Collectors.toList());
        log.info("알림 생성 완료 총 알림 수 = {}", notifications.size());
        for (Notification notification : notifications) {
            eventPublisher.publishEvent(
                    new SseNotificationEvent(
                            List.of(notification.getReceiver().getId()),
                            "notifications.created",
                            notificationMapper.toDto(notification)
                    )
            );
        }
        return notifications;
    }

    @Override
    @CacheEvict(value = "notifications", key = "#user.getId()")
    public Notification create(User user, Role oldRole, Role newRole) {
        log.info("권한 변경 알림 생성 호출");
        Notification notification = new Notification(
                user,
                "권한이 변경되었습니다.",
                String.format("%s -> %s", oldRole, newRole));
        log.info("권한 변경 알림 생성 완료");
        log.debug("{} 권한 변경 {} -> {}", user.getUsername(), oldRole, newRole);
        notificationRepository.save(notification);
        eventPublisher.publishEvent(
                new SseNotificationEvent(
                        List.of(notification.getReceiver().getId()),
                        "notifications.created",
                        notificationMapper.toDto(notification)
                )
        );
        return notification;
    }

    @Override
    public void create(DiscodeitException e) {
        log.info("S3 업로드 실패 알림 생성 호출");
        String errorMessage = e.getMessage();
        String content = String.format(
                """
                        RequestId: %s\s
                        BinaryContentId: %s\s
                        Error: %s
                        """,
                e.getDetails().get("RequestId"),
                e.getDetails().get("BinaryContentId"),
                errorMessage);

        List<User> admins = userRepository.findAllByRole(Role.ADMIN);

        admins.forEach(admin -> {
            Notification notification = new Notification(
                    admin,
                    "S3 파일 업로드 실패",
                    content
            );
            eventPublisher.publishEvent(
                    new SseNotificationEvent(
                            List.of(notification.getReceiver().getId()),
                            "notifications.created",
                            notificationMapper.toDto(notification)
                    )
            );
            notificationRepository.save(notification);
            cacheManager.getCache("notifications").evict(admin.getId());
        });
        log.info("S3 업로드 실패 알림 생성 완료");
    }

    @Override
    @Cacheable(value = "notifications", key = "#userId")
    public List<Notification> findAllByUserId(UUID userId) {
        log.info("알림 조회 호출");
        List<Notification> notifications =
                notificationRepository.findAllByReceiver_Id(userId);
        log.info("총 알림 수 = {}", notifications.size());
        return notifications;
    }

    @PreAuthorize("#user.getId() == authentication.principal.userDto.id")
    @CacheEvict(value = "notifications", key = "#user.getId()")
    @Override
    public void delete(User user, UUID id) {
        log.info("알림 삭제 호출");
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("알림이 존재하지 않습니다. notificationId = {}", id);
                    throw new NotificationNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of("notificationId", id));
                });
        notificationRepository.delete(notification);
        log.debug("알림 삭제 id = {}", notification.getId());
    }
}
