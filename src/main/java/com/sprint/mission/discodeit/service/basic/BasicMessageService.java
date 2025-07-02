package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import com.sprint.mission.discodeit.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;

    @Override
    public MessageResponseDto create(MessageCreateDto dto) {

        getChannelOrThrow(dto.getChannelId());
        getAuthorOtThrow(dto.getAuthorId());

        if (dto.getAttachmentIds() != null) {
            for (UUID attachmentId : dto.getAttachmentIds()) {
                getBinaryContentOrThrow(attachmentId);
            }
        }

        Message message = messageMapper.toEntity(dto);
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageResponseDto> findByChannelId(UUID channelId) {

        getChannelOrThrow(channelId);

        return messageRepository.findByChannelId(channelId)
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    public Message update(UUID messageId, String newContent) {

        Message message = getMessageOrThrow(messageId);

        boolean anyValueUpdated = false;

        anyValueUpdated = UpdateUtil.updateIfChanged(
                newContent,
                message.getContent(),
                message::setContent,
                anyValueUpdated
        );

        if (anyValueUpdated) {
            message.setUpdatedAt(Instant.now());
        }

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {

       Message message = getMessageOrThrow(messageId);

        messageRepository.deleteById(messageId);
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }
    }

    // 중복 메서드
    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    private User getAuthorOtThrow(UUID authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author not found with id " + authorId));
    }

    private BinaryContent getBinaryContentOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found with id " + binaryContentId));
    }

    private Message getMessageOrThrow(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found with id " + messageId));
    }
}
