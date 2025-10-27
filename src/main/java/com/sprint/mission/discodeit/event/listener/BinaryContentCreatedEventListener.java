package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage storage;
  private final BinaryContentService binaryContentService;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @Async("customTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBinaryContentCreated(BinaryContentCreatedEvent event) {

    log.debug("[BinaryContentCreatedEventListener] onBinaryContent id: {}, fileName: {}",
        event.binaryContent().getId(),
        event.binaryContent().getFileName());

    UUID id = event.binaryContent().getId();
    byte[] data = event.data();

    // 바이너리 데이터 저장
    try {
      storage.put(id, event.data());
      binaryContentService.updateStatus(id, BinaryContentStatus.SUCCESS);

      log.info("[BinaryContentCreatedEventListener] 바이너리 데이터 저장 성공: {}", id);
    } catch (Exception e) {
      log.error("[BinaryContentCreatedEventListener] 바이너리 데이터 저장 성공: {}, 실패 이유: {}", id,
          e.getMessage(), e);

      binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);
    }
  }

  @Async("customTaskExecutor")
  @EventListener
  public void onS3UploadFailed(S3UploadFailedEvent event) {

    String content =
        "Request Id: " + event.requestId() + "\n BinaryContentId: " + event.binaryContentId()
            + "\n Error: "
            + event.errorMessage();

    List<Notification> notifications = userRepository.findAllByRole(Role.ADMIN).stream()
        .map(user -> new Notification("S3 파일 업로드 실패", content, user))
        .toList();

    notificationRepository.saveAll(notifications);

    log.debug("[NotificationRequiredEventListener] S3 업로드 실패 알림 전송 완료- {}개",
        notifications.size());
  }
}
