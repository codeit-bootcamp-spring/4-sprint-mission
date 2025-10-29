package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    //
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //
    private final CacheManager cacheManager;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "#receiverId")
    public List<NotificationDto> getNotifications(UUID receiverId) {
        log.debug("알림 조회 시작: receiverId={}", receiverId);
        List<NotificationDto> notifications = notificationRepository.findByReceiverId(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();

        log.info("알림 조회 완료: receiverId={}, count={}", receiverId, notifications.size());
        return notifications;
    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId) {
        log.debug("알림 삭제 시작: id={}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withNotificationId(notificationId));

        UUID receiverId = notification.getReceiver().getId();
        notificationRepository.delete(notification);

        Objects.requireNonNull(cacheManager.getCache("notifications")).evict(receiverId);
        log.info("알림 삭제 완료 id={}", notificationId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createMessageNotification(MessageCreatedEvent event) {
        log.debug("메시지 생성 알람 생성 시작 event={}", event);
        MessageDto messageDto = event.messageDto();
        UserDto authorDto = messageDto.author();
        UUID authorId = authorDto.id();
        UUID channelId = messageDto.channelId();

        List<User> receivers = readStatusRepository.findAllByChannelIdWithUser(channelId)
                .stream()
                .filter(ReadStatus::isNotificationEnabled)
                .map(ReadStatus::getUser)
                .filter(user -> !user.getId().equals(authorId)) // 작성자는 제외
                .toList();

        // Notification 객체 생성 준비
        String authorUsername = authorDto.username();
        String channelName = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId))
                .getName();

        // Notification 객체 생성, 저장
        for (User receiver : receivers) {
            String title = authorUsername + " (#" + channelName + ")";
            String content = messageDto.content();
            Notification notification = new Notification(receiver, title, content);
            notificationRepository.save(notification);

            Objects.requireNonNull(cacheManager.getCache("notifications")).evict(receiver.getId());
            log.debug("메시지 생성 알람 생성 성공: id={}", notification.getId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createRoleUpdatedNotification(RoleUpdatedEvent event) {
        log.debug("권한 변경 알람 생성 시작: event={}", event);
        User receiver = event.user();

        String title = "권한이 변경되었습니다.";
        String content = event.oldRole().name() + " -> " + event.newRole().name();
        Notification notification = new Notification(receiver, title, content);
        notificationRepository.save(notification);

        Objects.requireNonNull(cacheManager.getCache("notifications")).evict(receiver.getId());
        log.debug("권한 변경 알람 생성 성공: id={}", notification.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createS3UploadFailedNotification(S3UploadFailedEvent event) {
        log.debug("바이너리 컨텐츠 생성 실패 알람 생성 시작: event={}", event);
        User receiver = userRepository.findByUsername("admin")
                .orElseThrow(() -> UserNotFoundException.withUsername("admin"));

        String taskName = event.taskName();
        String requestId = event.requestId();
        String binaryContentId = event.binaryContentId().toString();
        String errorMessage = event.errorMessage();

        String title = taskName + " 실패";

        String content = "RequestId: " + requestId + "\n" +
                        "BinaryContentId: " + binaryContentId + "\n" +
                        "Error: " + errorMessage;

        Notification notification = new Notification(receiver, title, content);
        notificationRepository.save(notification);

        Objects.requireNonNull(cacheManager.getCache("notifications")).evict(receiver.getId());
        log.debug("바이너리 컨텐츠 생성 실패 알람 생성 성공: id={}", notification.getId());
    }
}
