package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailureEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        List<ReadStatus> readStatuses = readStatusRepository.findReadStatusByChannelId(event.getChannelId());
        User author = userRepository.findById(event.getAuthorId())
                        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        Channel channel = channelRepository.findById(event.getChannelId())
                        .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));
        String title = String.format("%s (#%s)", author.getUsername(), channel.getName());

        readStatuses.stream()
                .filter(readStatus -> !readStatus.getUserId().equals(event.getAuthorId()))
                .forEach(readStatus ->
                        notificationService.create(readStatus.getUser(),title,event.getContent()));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        User changedUser = userRepository.findById(event.getChangedUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", event.getOldRole().name(), event.getNewRole().name());
        notificationService.create(changedUser, title, content);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void on (BinaryContentUploadFailureEvent event) {
        userRepository.findAllByRole(Role.ADMIN)
                        .forEach(
                                admin ->
                                {
                                    String Content = String.format("""
                                        RequestId: %s
                                        BinaryContentId: %s
                                        Error: %s""",event.getRequestId(),event.getBinaryContentId(),event.getReason());
                                    notificationService.create(admin, "Binary Content 업로드 실패", Content);
                                }
                        );
    }

}
