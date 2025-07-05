package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final ChannelRepository channelRepository;

    @Override
    public MessageResponseDto createMessage(MessageCreateDto messageCreateDto) {
        User user =
                userRepository
                        .findById(messageCreateDto.userId())
                        .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));
        Channel channel =
                channelRepository
                        .findById(messageCreateDto.channelId())
                        .orElseThrow(() -> new RuntimeException("해당 채널이 존재하지 않습니다."));

        Message message = messageMapper.messageCreateDtoToMessage(messageCreateDto);

        user.getMessageIds().add(message.getId());
        channel.getMessageIds().add(message.getId());

        if (messageCreateDto.binaryContents() != null && !messageCreateDto.binaryContents().isEmpty()) {
            for (BinaryContentRequestDto binaryContentRequestDto : messageCreateDto.binaryContents()) {
                BinaryContent binaryContent =
                        new BinaryContent(
                                null,
                                message.getId(),
                                binaryContentRequestDto.data(),
                                binaryContentRequestDto.dataType());
                binaryContentRepository.save(binaryContent);
                message.getAttachmentIds().add(binaryContent.getId());
            }
        }
        messageRepository.save(message);

        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .filter(message -> message.getState() != MessageState.DELETED)
                .map(messageMapper::messageToMessageResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDto findMessageById(UUID messageId) {
        Message message =
                messageRepository
                        .findById(messageId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 메세지가 존재하지 않습니다."));
        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findMessagesByContentContains(String MessageName) {
        return messageRepository.findMessagesByContentContains(MessageName).stream()
                .map(messageMapper::messageToMessageResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDto updateMessage(MessageUpdateDto messageUpdateDto) {
        Message message = isExistMessage(messageUpdateDto.id());

        Optional.ofNullable(messageUpdateDto.content()).ifPresent(message::setContents);

        Optional.ofNullable(messageUpdateDto.removeAttachments())
                .filter(attachments -> !attachments.isEmpty())
                .ifPresent(
                        removeList -> {
                            removeList.forEach(
                                    attachmentId -> {
                                        binaryContentRepository.delete(attachmentId);
                                        message.getAttachmentIds().remove(attachmentId);
                                    });
                        });

        Optional.ofNullable(messageUpdateDto.newAttachments())
                .filter(attachments -> !attachments.isEmpty())
                .ifPresent(
                        addList -> {
                            addList.forEach(
                                    binaryContentDto -> {
                                        BinaryContent binaryContent =
                                                binaryContentMapper.BinaryContentRequestDtoToBinaryContent(
                                                        binaryContentDto, messageUpdateDto.id());
                                        binaryContentRepository.save(binaryContent);
                                        message.getAttachmentIds().add(binaryContent.getId());
                                    });
                        });

        messageRepository.save(message);
        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = isExistMessage(messageId);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(message.getId());
    }

    public Message isExistMessage(UUID messageId) {
        return messageRepository
                .findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }

    public User isExistUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}
