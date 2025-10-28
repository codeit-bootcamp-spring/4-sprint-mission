package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) {
        UUID messageId = event.getMessageId();
        UUID channelId = event.getChannelId();
        UUID senderId  = event.getSenderId();

        // 메시지/채널 로드 (내용/이름/보낸사람 표시용)
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException("메시지를 찾을 수 없습니다: id=" + messageId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalStateException("채널을 찾을 수 없습니다: id=" + channelId));
        String senderName = message.getAuthor().getUsername();
        String channelName = channel.getName();
        String title = senderName + " (#" + channelName + ")";
        String content = message.getContent();

        // 채널의 알림 활성 대상 조회
        List<ReadStatus> targets =
                readStatusRepository.findAllByChannel_IdAndNotificationEnabledTrue(channelId);

        // 보낸 사람 제외하고 알림 생성
        List<Notification> notifications = targets.stream()
                .map(ReadStatus::getUser)
                .filter(u -> !u.getId().equals(senderId))
                .map(u -> new Notification(u, title, content))
                .toList();

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            log.info("메시지 알림 생성: channelId={}, messageId={}, targets={}",
                    channelId, messageId, notifications.size());
        } else {
            log.debug("메시지 알림 대상 없음(모두 비활성 또는 발신자만 존재): channelId={}, messageId={}",
                    channelId, messageId);
        }
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        UUID userId = event.getUserId();

        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: id=" + userId));

        String title = "권한이 변경되었습니다.";
        String content = event.getPreviousRole().name() + " -> " + event.getNewRole().name();

        notificationRepository.save(new Notification(receiver, title, content));
        log.info("권한 변경 알림 생성: userId={}, {}", userId, content);
    }
}

