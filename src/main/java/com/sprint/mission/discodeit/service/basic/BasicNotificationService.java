package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final SseService sseService;

    @Override
    @Cacheable(value = "notifications", key = "#userId")
    public List<NotificationDto> findAllByReceiverId(UUID userId) {
        User receiver = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException().withId(userId));
        List<Notification> notifications = notificationRepository.findAllByReceiverId(receiver.getId());
        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public void deleteByIdAndReceiverId(UUID id, UUID receiverId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(()->new RuntimeException("알림이 없습니다."));
        if (!notification.getReceiverId().equals(receiverId)) {
            throw new RuntimeException("알림 확인 권한이 없습니다.");
        }
        notificationRepository.delete(notification);
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public void createNotification(NotificationDto dto) {
        Notification notification = new Notification(
                dto.receiverId(),
                dto.title(),
                dto.content()
        );
        notificationRepository.save(notification);

        // 알림 생성 시 SSE 이벤트 전송
        sseService.send(
                List.of(dto.receiverId()), //수신자 ID 묶음
                "notifications.created",  // 이벤트 이름
                notificationMapper.toDto(notification)  // 전송 데이터
        );
    }
}
