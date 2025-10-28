package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final UserRepository userRepository;

  public BasicNotificationService(NotificationRepository notificationRepository,
      NotificationMapper notificationMapper, UserRepository userRepository) {
    this.notificationRepository = notificationRepository;
    this.notificationMapper = notificationMapper;
    this.userRepository = userRepository;
  }

  @Cacheable("notifications")
  @Override
  public List<NotificationDto> getNotifications(UUID userId) {
    List<Notification> notifications = notificationRepository.findAllByReceiverId(userId);
    List<NotificationDto> notificationDtos = notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
    return notificationDtos;
  }

  @Override
  public Optional<NotificationDto> getNotification(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
    return Optional.ofNullable(notificationMapper.toDto(notification));
  }

  @CacheEvict(value = "notifications", allEntries = true)
  @Override
  public void deleteNotification(UUID notificationId) {
    notificationRepository.deleteById(notificationId);
  }

  @CacheEvict(value = "notifications", allEntries = true)
  @Override
  public void notifyFailure(String requestId, UUID binaryContentId, String errorMessage) {
    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    String title = "S3 파일 업로드 실패";
    String content = "RequestId: " + requestId + "\n"
        + "BinaryContentId: " + binaryContentId + "\n"
        + "Error: " + errorMessage + "\n";
    admins.forEach(user -> {
      Notification notification = new Notification(user, title, content);
      notificationRepository.save(notification);
    });
  }
}
