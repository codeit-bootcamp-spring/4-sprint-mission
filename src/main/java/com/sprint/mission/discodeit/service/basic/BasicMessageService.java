package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    @Autowired(required = false)
    private BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public Message createMessage(MessageCreateRequest messageCreateRequest, List<UUID> attachments) {
        log.debug("메시지 생성 호출");
        Channel channel = validateChannel(messageCreateRequest.channelId());
        User user = validateUser(messageCreateRequest.authorId());

        Message message = new Message(messageCreateRequest.content(), channel, user);

        if (attachments != null && !attachments.isEmpty()) {
            attachments.stream()
                    .forEach(binaryContentId -> {
                        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                                .orElse(null);
                        message.getAttachments().add(binaryContent);
                    });
        }
        messageRepository.save(message);
        log.info("메시지 생성 완료 id = {}", message.getId());
        return message;
    }

    @Override
    public Page<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        validateChannel(channelId);
        Page<Message> messages = messageRepository.findAllByChannel_Id(channelId, pageable);
        return messages;
    }

    @Transactional
    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        log.debug("메시지 수정 호출 id = {}", messageId);
        Message findMessage = validateMessage(messageId);

        Optional.ofNullable(messageUpdateRequest.newContent())
                .ifPresent(findMessage::setContent);

        messageRepository.save(findMessage);
        log.info("메시지 수정 완료 id = {}", messageId);
        return findMessage;
    }

    @Transactional
    @Override
    public void removeMessage(UUID messageId) {
        log.debug("메시지 삭제 호출 id = {}", messageId);
        Message findMessage = validateMessage(messageId);

        messageRepository.delete(findMessage);
        log.info("메시지 삭제 완료 id = {}", messageId);
        if (findMessage.getAttachments() != null && !findMessage.getAttachments().isEmpty()) {
            findMessage.getAttachments().stream()
                    .forEach(bc -> {
                        binaryContentRepository.delete(bc);
                        log.debug("첨푸파일 삭제 완료 id = {}", messageId);
                    });
        }
    }

    private Message validateMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("해당 메시지를 찾을 수 없음 id = {}", messageId);
                    throw new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
                });
    }

    private User validateUser(UUID authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("해당 유저를 찾을 수 없음 id = {}", authorId);
                    throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", authorId));
                });
    }

    private Channel validateChannel(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.warn("해당 채널을 찾을 수 없음 id = {}", channelId);
                    throw new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND, Map.of("channelId", channelId));
                });
    }
}
