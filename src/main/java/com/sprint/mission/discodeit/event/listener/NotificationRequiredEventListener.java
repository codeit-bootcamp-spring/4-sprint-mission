/*
package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void on(MessageCreatedEvent event) {
        // 같은 채널의 참가자 중 sender 제외 각 사용자별 "알림 생성" -> DB에 notification 저장
        log.debug("# MessageCreatedEvent 수신 시작");

        UUID channelId = event.channelId();
        UUID senderId = event.userId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> UserNotFoundException.withId(senderId));

        List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(channelId);
        List<User> receivers = readStatuses.stream()
                .map(ReadStatus::getUser)
                .filter(user -> !user.getId().equals(senderId))
                .toList();

        for (User receiver : receivers) {
            Notification notification = new Notification(receiver, sender, channel, event.content());
            notificationRepository.save(notification);
        }

        log.info(" # MessageCreatedEvent 수신 완료, 알림 저장 완료");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void on(BinaryContentSaveFailEvent event) {
        // 바이너리 컨텐츠 업로드 실패 시 admin 에게 알림 생성 -> DB에 알림 저장
        log.debug("### [이벤트 리스너] RecoverFailMessageEvent 캐치");

        User admin = userRepository.findByRole(Role.ADMIN);
        User sender = userRepository.findById(event.getSenderId())
                .orElseThrow(() -> UserNotFoundException.withId(event.getSenderId()));
        String content = String.format("""
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """, event.getRequestId(), event.getBinaryContentId(), event.getErrorMessage());

        Notification notification = new Notification(
                admin,
                sender,
                null,
                content
        );
        notificationRepository.save(notification);
        log.info("### 관리자에게 실패 알림 발송 완료");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")
    public void on(RoleUpdatedEvent event) {
        // role이 바뀌면 알림 생성 -> DB에 알림 저장
        log.debug("# RoleUpdatedEvent 리스너 시작");

        UUID receiverId = event.userId();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> UserNotFoundException.withId(receiverId));
        User sender = userRepository.findByRole(Role.ADMIN);
        Notification notification = new Notification(receiver, sender, null, "권한이 변경되었습니다.");
        notificationRepository.save(notification);
        log.info("# RoleUpdatedEvent 리스너 완료, 알람 저장");
    }
}
*/
