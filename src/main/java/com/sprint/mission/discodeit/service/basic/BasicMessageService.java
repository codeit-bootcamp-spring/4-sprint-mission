package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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
    private final BinaryContentMapper binaryContentMapper;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponseDto createMessage(MessageCreateRequest messageCreateRequest, List<BinaryContentPostDto> attachments) {
        validateChannel(messageCreateRequest.channelId());
        Channel channel = channelRepository.findById(messageCreateRequest.channelId());
        validateUser(messageCreateRequest.authorId());

        Message message = messageMapper.toMessage(messageCreateRequest);

        if (attachments != null && !attachments.isEmpty()) {
            attachments.stream()
                    .filter(binaryContent -> binaryContent != null)
                    .forEach(binaryContent -> {
                        BinaryContent biContent = binaryContentMapper.toBinaryContent(binaryContent);
                        message.getAttachmentIds().add(biContent.getId());
                        binaryContentRepository.save(biContent);
                    });
        }
        message.setActive(ActiveStatus.ACTIVE);
        messageRepository.save(message);

        channel.addMessageToChannel(message);
        channelRepository.save(channel);
        MessageResponseDto messageResponseDto = messageMapper.toMessageResponseDto(message);
        return messageResponseDto;
    }

    @Override
    public List<MessageResponseDto> findallByChannelId(UUID channelId) {
        List<MessageResponseDto> messageResponseDtos = new ArrayList<>();
        messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> messageResponseDtos.add(messageMapper.toMessageResponseDto(message)));

        return messageResponseDtos;
    }

    @Override
    public MessageResponseDto getMessagesById(UUID messageId) {
        return messageMapper.toMessageResponseDto(messageRepository.findById(messageId));
    }

    @Override
    public MessageResponseDto updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        Message findMessage = messageRepository.findById(messageId);
        validateActiveMessage(findMessage);

        Optional.ofNullable(messageUpdateRequest.newContent()).ifPresent(findMessage::setContent);
        findMessage.setUpdatedAt(Instant.now());

        messageRepository.save(findMessage);

        return messageMapper.toMessageResponseDto(findMessage);
    }

    @Override
    public List<MessageResponseDto> getActiveMessages() {
        List<MessageResponseDto> activeMessageResponseDtos = new ArrayList<>();
        messageRepository.findAllActive().stream()
                .forEach(message -> activeMessageResponseDtos.add(messageMapper.toMessageResponseDto(message)));
        return activeMessageResponseDtos;
    }

    @Override
    public List<MessageResponseDto> findAll() {
        List<MessageResponseDto> messageResponseDtos = new ArrayList<>();
        messageRepository.findAll().stream()
                .forEach(message -> messageResponseDtos.add(messageMapper.toMessageResponseDto(message)));
        return messageResponseDtos;
    }

    @Override
    public void removeMessage(UUID messageId) {
        Message findMessage = messageRepository.findById(messageId);
        validateActiveMessage(findMessage);

        findMessage.setActive(ActiveStatus.DELETE);
        messageRepository.delete(findMessage);
        if (findMessage.getAttachmentIds() != null && !findMessage.getAttachmentIds().isEmpty()) {
            findMessage.getAttachmentIds().stream()
                    .forEach(id -> {
                        binaryContentRepository.delete(id);
                    });
        }
        Channel channel = channelRepository.findById(findMessage.getChannelId());
        channel.removeMessageFromChannel(findMessage);
        channelRepository.save(channel);
    }

    private void validateActiveMessage(Message message) {
        if (!message.getActive().equals(ActiveStatus.ACTIVE))
            throw new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND);
    }

    private void validateUser(UUID authorId) {
        if (userRepository.findAll().stream()
                .noneMatch(user -> user.getId().equals(authorId)))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }

    private void validateChannel(UUID channelId) {
        if (channelRepository.findAll().stream()
                .noneMatch(channel -> channel.getId().equals(channelId)))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_OR_USER_NOT_FOUND);
    }
}
