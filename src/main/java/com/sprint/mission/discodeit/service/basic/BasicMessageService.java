package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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
        validateChannel(messageCreateRequest.channelId());
        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
        validateUser(messageCreateRequest.authorId());
        User user = userRepository.findById(messageCreateRequest.authorId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
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
        return message;
    }

    @Override
    public Page<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
        Page<Message> messages = messageRepository.findAllByChannel_Id(channelId, pageable);
        return messages;
    }

    @Transactional
    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        Message findMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
        validateActiveMessage(findMessage);

        Optional.ofNullable(messageUpdateRequest.newContent())
                .ifPresent(findMessage::setContent);

        messageRepository.save(findMessage);

        return findMessage;
    }

    @Transactional
    @Override
    public void removeMessage(UUID messageId) {
        Message findMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
        validateActiveMessage(findMessage);

        messageRepository.delete(findMessage);
        if (findMessage.getAttachments() != null && !findMessage.getAttachments().isEmpty()) {
            findMessage.getAttachments().stream()
                    .forEach(bc -> {
                        binaryContentRepository.delete(bc);
                    });
        }
        Channel channel = channelRepository.findById(findMessage.getChannelId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
        channelRepository.save(channel);
    }

    private void validateActiveMessage(Message message) {
        if (!messageRepository.existsById(message.getId()))
            throw new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND);
    }

    private void validateUser(UUID authorId) {
        if (!userRepository.existsById(authorId))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }

    private void validateChannel(UUID channelId) {
        if (!channelRepository.existsById(channelId))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }
}
