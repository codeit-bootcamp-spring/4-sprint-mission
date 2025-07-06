package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;
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

    @Override
    public MessageResponseDto create(MessageCreateDto dto) {
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        Message message = MessageMapper.dtoToEntity(dto, new ArrayList<>());
        messageRepository.save(message);

        List<UUID> attachmentIds = new ArrayList<>();
        List<BinaryContentResponseDto> binaryContents = new ArrayList<>();

        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (MultipartFile file : dto.getAttachments()) {
                try {
                    BinaryContent content = new BinaryContent(
                            dto.getAuthorId(),
                            message.getId(),
                            file.getBytes(),
                            file.getOriginalFilename(),
                            file.getContentType());

                    BinaryContent saved = binaryContentRepository.save(content);
                    attachmentIds.add(saved.getId());

                    binaryContents.add(new BinaryContentResponseDto(
                            saved.getId(),
                            saved.getUserId(),
                            saved.getMessageId(),
                            saved.getDatas(),
                            saved.getFilename(),
                            saved.getFileType()
                    ));
                } catch (IOException e) {
                    throw new RuntimeException("파일을 업로드 중 오류발생", e);
                }
            }
        }

        message.updateAttachmentIds(attachmentIds);
        messageRepository.save(message);

        return MessageMapper.entityToDto(message, binaryContents);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        List<MessageResponseDto> result = new ArrayList<>();

        for (Message message : messages) {
            List<BinaryContentResponseDto> attachments = new ArrayList<>();
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.findById(attachmentId)
                        .ifPresent(content ->
                                attachments.add(new BinaryContentResponseDto(
                                        content.getId(),
                                        content.getUserId(),
                                        content.getMessageId(),
                                        content.getDatas(),
                                        content.getFilename(),
                                        content.getFileType()
                                ))
                        );
            }

            result.add(MessageMapper.entityToDto(message, attachments));
        }
        return result;
    }

    @Override
    public MessageResponseDto update(UUID messageId, MessageUpdateDto dto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found : " + messageId));

        message.update(dto.getNewContent());
        messageRepository.save(message);

        List<BinaryContentResponseDto> attachments = message.getAttachmentIds().stream()
                .map(binaryContentRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(content -> new BinaryContentResponseDto(
                        content.getId(),
                        content.getUserId(),
                        content.getMessageId(),
                        content.getDatas(),
                        content.getFilename(),
                        content.getFileType()
                ))
                .toList();

        return MessageMapper.entityToDto(message, attachments);
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
