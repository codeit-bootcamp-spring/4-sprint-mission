package com.sprint.mission.discodeit.event.listner;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final NotificationRepository notificationRepository;

    @Async
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.channelId(), true);
        User author = userRepository.findById(event.authorId()).orElseThrow(() -> new UserNotFoundException().withId(event.authorId()));
        Channel channel = channelRepository.findById(event.channelId()).orElseThrow(() -> new ChannelNotFoundException().withId(event.channelId()));
        for (ReadStatus readStatus : readStatuses) {
            UUID receiverId = readStatus.getUser().getId();
            if (receiverId.equals(event.authorId())) continue;

            Notification notification = new Notification(
                    receiverId,
                    String.format("%s (#%s)",
                            author.getUsername(),
                            channel.getName()),
                    event.content()
            );
            notificationRepository.save(notification);
        }
    }

    @Async
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        User receiver = userRepository.findById(event.receiverId())
                .orElseThrow(() -> new UserNotFoundException().withId(event.receiverId()));

        Notification notification = new Notification(
                receiver.getId(),
                "권한이 변경되었습니다",
                String.format("%s → %s",
                        event.previousRole(),
                        event.newRole())
        );
        notificationRepository.save(notification);
    }

    @Async
    @TransactionalEventListener
    public void on(S3UploadFailedEvent event) {
        String content = String.format("""
                S3 파일 업로드 실패
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """, event.requestId(), event.binaryContentId(), event.errorMessage());

        User admin = userRepository.findByEmail(("admin@gmail.com"))
                .orElseThrow(()-> new UserNotFoundException());

        Notification notification = new Notification(
            admin.getId(),
                "시스템 알림",
                content
        );
        notificationRepository.save(notification);
    }
}
