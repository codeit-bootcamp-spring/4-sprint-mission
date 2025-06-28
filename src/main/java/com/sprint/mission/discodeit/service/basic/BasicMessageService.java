package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageMapper messageMapper;
    private final BinaryContentMapper binaryContentMapper;
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    /* 오프라인 상태인 유저는 메시지를 작성할 수 없습니다.
    *  유저의 상태 정보는 UserStatus를 통해서만 조회가 가능하므로,
    * 특별히 UserStatusRepository에 대한 의존성을 추가합니다. */
    private final UserStatusRepository userStatusRepository;

    @Override
    public MessageResponseDto create(MessageCreateDto messageCreateDto) {
        if (!userRepository.existsById(messageCreateDto.authorId())) {
            throw new NoSuchElementException("Author not found with id " + messageCreateDto.authorId());
        }

        if (!channelRepository.existsById(messageCreateDto.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + messageCreateDto.channelId());
        }

        // OFFLINE 유저는 메시지를 작성할 수 없다.
        UserStatus authorStatus = userStatusRepository.findByUserId(messageCreateDto.authorId())
                .orElseThrow(() -> new NoSuchElementException(this.getClass().getSimpleName() + ".create() 실패: 해당 id에 해당하는 유저의 상태 정보가 없습니다."));

        if (authorStatus.checkUserOnline() == UserStatusType.OFFLINE) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".create() 실패: OFFLINE 상태인 유저는 메시지를 작성할 수 없습니다.");
        }

        Message createdMessage = messageMapper.messageCreateDtoToMessage(messageCreateDto);

        // BinaryContent(Attachment)가 존재한다면...
        if (!messageCreateDto.binaryContentCreateDtos().isEmpty()) {
            List<UUID> attachmentIds = saveAttachments(messageCreateDto.binaryContentCreateDtos(), createdMessage.getId(), createdMessage.getAuthorId());
            createdMessage.setAttachmentIds(attachmentIds);
        }

        messageRepository.save(createdMessage);
        return messageMapper.messageToMessageResponseDto(createdMessage);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<MessageResponseDto> messageResponseDtos = new ArrayList<>();

        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
        for (Message message : messages) {
            messageResponseDtos.add(messageMapper.messageToMessageResponseDto(message));
        }
        return messageResponseDtos;
    }

    @Override
    public MessageResponseDto update(MessageUpdateDto messageUpdateDto) {
        Message updatedMessage = messageRepository.findById(messageUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageUpdateDto.id() + " not found"));

        // 해당 메시지를 작성한 유저만 수정할 수 있다.
        if (!messageUpdateDto.authorId().equals(updatedMessage.getAuthorId())) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".update() 실패: 메시지 작성자가 아니면 수정할 수 없습니다.");
        }

        // 메시지를 수정할 유저가 OFFLINE 상태면 메시지를 수정할 수 없다.
        UserStatus authorStatus = userStatusRepository.findByUserId(messageUpdateDto.authorId())
                .orElseThrow(() -> new NoSuchElementException(this.getClass().getSimpleName() + ".update() 실패: 해당 id에 해당하는 유저의 상태 정보가 없습니다."));

        if (authorStatus.checkUserOnline() == UserStatusType.OFFLINE) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".update() 실패: OFFLINE 상태인 유저는 메시지를 작성할 수 없습니다.");
        }

        // 수정할 BinaryContent(Attachment)가 존재한다면..
        if (messageUpdateDto.nweBinaryContentCreateDtos() != null) {
            List<UUID> newAttachmentIds = saveAttachments(messageUpdateDto.nweBinaryContentCreateDtos(), messageUpdateDto.id(), updatedMessage.getAuthorId());
            updatedMessage.update(messageUpdateDto.newContent(), newAttachmentIds);
        } else {
            updatedMessage.update(messageUpdateDto.newContent(), null);
        }
        messageRepository.save(updatedMessage);
        return messageMapper.messageToMessageResponseDto(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
        Message deletedMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        for (UUID attachmentId : deletedMessage.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }
        messageRepository.deleteById(messageId);
    }

    private List<UUID> saveAttachments(List<BinaryContentCreateDto> dtos, UUID messageId, UUID authorId) {
        List<UUID> attachmentIds = new ArrayList<>();

        for (BinaryContentCreateDto dto : dtos) {
            BinaryContent createdBinaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(dto);
            createdBinaryContent.setMessageId(messageId);
            createdBinaryContent.setUserId(authorId);

            binaryContentRepository.save(createdBinaryContent);
            attachmentIds.add(createdBinaryContent.getId());
        }
        return attachmentIds;
    }
}
