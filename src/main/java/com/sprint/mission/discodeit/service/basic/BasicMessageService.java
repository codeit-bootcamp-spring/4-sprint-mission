package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.MessageResponse;
import com.sprint.mission.discodeit.DTO.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private MessageResponse toDto(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        List<UUID> attachmentIds = null;
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            attachmentIds = request.attachments().stream()
                    .map(BinaryContent::new)
                    .map(binaryContentRepository::save)
                    .toList();
        }

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                attachmentIds
        );

        return toDto(messageRepository.save(message));
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return toDto(message);
    }

    @Override
    public List<MessageResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));
        message.update(request.newContent());
        return toDto(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (UUID attachmentId : attachmentIds) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }
        messageRepository.deleteById(messageId);
    }
}

