package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailureEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                    MessageCreatedEvent.class);
            List<ReadStatus> readStatuses = readStatusRepository.findReadStatusByChannelId(event.getChannelId());
            User author = userRepository.findById(event.getAuthorId())
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
            Channel channel = channelRepository.findById(event.getChannelId())
                    .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

            String channelName = getMessageSource(channel, author);
            String title = String.format("%s (#%s)", author.getUsername(), channelName);

            readStatuses.stream()
                    .filter(readStatus -> !readStatus.getUserId().equals(event.getAuthorId()))
                    .forEach(readStatus ->
                            notificationService.create(readStatus.getUser(),title,event.getContent()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getMessageSource(Channel channel, User author) {
        return channel.getType() == ChannelType.PRIVATE ? author.getUsername() : channel.getName();
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
                    RoleUpdatedEvent.class);
            User changedUser = userRepository.findById(event.getChangedUserId())
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
            String title = "권한이 변경되었습니다.";
            String content = String.format("%s -> %s", event.getOldRole().name(), event.getNewRole().name());
            notificationService.create(changedUser, title, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.BinaryContentUploadFailureEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            BinaryContentUploadFailureEvent event = objectMapper.readValue(kafkaEvent,
                    BinaryContentUploadFailureEvent.class);
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
