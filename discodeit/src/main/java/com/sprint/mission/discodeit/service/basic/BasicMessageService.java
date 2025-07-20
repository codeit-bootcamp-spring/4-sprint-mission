package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponse create(MessageCreateRequest dto, List<MultipartFile> attachments) {
        userRepository.findById(dto.authorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        channelRepository.findById(dto.channelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        Message message = new Message(
                UUID.randomUUID(),
                dto.authorId(),
                dto.channelId(),
                dto.content(),
                new ArrayList<>()
        );
        messageRepository.save(message);

        List<UUID> attachmentIds = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (file != null && !file.isEmpty()) {
                    try {
                        BinaryContent content = new BinaryContent(
                                dto.authorId(),
                                message.getId(),
                                file.getBytes(),
                                file.getOriginalFilename(),
                                file.getContentType()
                        );
                        BinaryContent saved = binaryContentRepository.save(content);
                        attachmentIds.add(saved.getId());
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 중 오류 발생", e);
                    }
                }
            }
        }

        message.updateAttachmentIds(attachmentIds);
        messageRepository.save(message);

        return messageMapper.toResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        for (Message message : messages) {
            if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
                List<UUID> ids = message.getAttachmentIds();
                List<BinaryContent> contents = ids.stream()
                        .map(id -> binaryContentRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();

                message.loadAttachments(contents);
            }
        }

        return messages.stream()
                .map(messageMapper::toResponse)
                .toList();
    }


    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequestDto dto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found : " + messageId));

        if (dto.newContent() != null && !dto.newContent().isBlank()) {
            message.updateContent(dto.newContent());
        }

        MultipartFile newFile = dto.newAttachment();
        boolean hasNewImage = newFile != null && !newFile.isEmpty();

        if (hasNewImage) {
            List<UUID> oldAttachmentIds = message.getAttachmentIds();
            if (oldAttachmentIds != null && !oldAttachmentIds.isEmpty()) {
                for (UUID fileId : oldAttachmentIds) {
                    binaryContentRepository.deleteById(fileId);
                }
            }

            try {
                BinaryContent newContent = new BinaryContent(
                        message.getAuthorId(),
                        message.getId(),
                        newFile.getBytes(),
                        newFile.getOriginalFilename(),
                        newFile.getContentType()
                );

                BinaryContent saved = binaryContentRepository.save(newContent);
                message.updateAttachmentIds(List.of(saved.getId()));
            } catch (IOException e) {
                throw new RuntimeException("파일 처리 중 오류 발생", e);
            }
        }

        messageRepository.save(message);
        return messageMapper.toResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found : " + messageId));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(messageId);
    }
}
