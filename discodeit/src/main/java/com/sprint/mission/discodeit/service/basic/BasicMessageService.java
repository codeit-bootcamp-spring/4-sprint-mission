package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
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
        List<BinaryContentDto> binaryContents = new ArrayList<>();

        if (dto.getAttachments() != null) {
            for (BinaryContentDto fileDto : dto.getAttachments()) {
                BinaryContent content = new BinaryContent(
                        dto.getAuthorId(),
                        message.getId(),
                        fileDto.getData(),
                        fileDto.getFileName(),
                        fileDto.getFileType()
                );
                BinaryContent saved = binaryContentRepository.save(content);
                attachmentIds.add(saved.getId());

                binaryContents.add(new BinaryContentDto(
                        saved.getUserId(),
                        saved.getMessageId(),
                        saved.getDatas(),
                        saved.getFilename(),
                        saved.getFileType()
                ));
            }
        }

        messageRepository.save(message);

        return MessageMapper.entityToDto(message, binaryContents);
    }

    @Override
    public List<MessageResponseDto> findallByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        List<MessageResponseDto> result = new ArrayList<>();

        for (Message message : messages) {
            List<BinaryContentDto> attachments = new ArrayList<>();
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.findById(attachmentId)
                        .ifPresent(content ->
                                attachments.add(new BinaryContentDto(
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
    public MessageUpdateDto update(MessageUpdateDto dto) {
        Message message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("Message not found : " + dto.getMessageId()));
        message.update(dto.getNewContent());
        messageRepository.save(message);

        return new MessageUpdateDto(
                message.getId(),
                message.getContent(),
                message.getUpdatedAt()
        );
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
