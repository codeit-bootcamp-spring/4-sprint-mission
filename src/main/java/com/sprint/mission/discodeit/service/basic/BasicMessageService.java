package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public MessageResponseDto create(MessageCreateDto createMessageDto) {
        validateChannelExists(createMessageDto.getChannelId());
        validateAuthorExists(createMessageDto.getAuthorId());

        Message message = messageMapper.toEntity(createMessageDto);
        try {
            handleAttachments(message, createMessageDto.getAttachments());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message =  requireMessage(messageId);
        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<Message> messageList = messageRepository.findAllByChannelId(channelId);
        return messageMapper.toDtoList(messageList);
    }

    @Override
    public MessageResponseDto update(MessageUpdateDto updateMessageDto) {
        Message message = requireMessage(updateMessageDto.getId());

        List<BinaryContentCreateDto> atts = updateMessageDto.getAttachments();
        if (atts != null && !atts.isEmpty()) {
            deleteExistingAttachments(message);
            try {
                handleAttachments(message, atts);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        messageMapper.updateEntity(updateMessageDto, message);
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = requireMessage(messageId);
        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            attachmentIds.forEach(binaryContentRepository::deleteById);
        }
        messageRepository.deleteById(messageId);
    }

    /* =========================================================
     * INTERNAL VALIDATION HELPERS
     * ========================================================= */

    /**
     * 채널 ID가 존재하는지 검증합니다.
     *
     * @param channelId 채널 ID
     * @throws NoSuchElementException 채널이 존재하지 않는 경우
     */
    private void validateChannelExists(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }
    }

    /**
     * 작성자(유저) ID가 존재하는지 검증합니다.
     *
     * @param authorId 작성자 ID
     * @throws NoSuchElementException 작성자가 존재하지 않는 경우
     */
    private void validateAuthorExists(UUID authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author not found with id " + authorId);
        }
    }

    /**
     * 메시지 ID가 존재하는지 검증합니다.
     *
     * @param messageId 메시지 ID
     * @throws NoSuchElementException 메시지가 존재하지 않는 경우
     */
    private void validateMessageExists(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message not found with id " + messageId);
        }
    }

    /**
     * 메시지를 ID로 조회하며, 존재하지 않을 경우 예외를 발생시킵니다.
     *
     * @param messageId 메시지 ID
     * @return 메시지 엔티티
     * @throws NoSuchElementException 메시지가 존재하지 않는 경우
     */
    private Message requireMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    private void handleAttachments(Message message, List<BinaryContentCreateDto> attachments) throws IOException {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        List<UUID> ids = new ArrayList<>();
        for (BinaryContentCreateDto bcDto : attachments) {
            BinaryContent bc = binaryContentMapper.toEntity(bcDto);
            binaryContentRepository.save(bc);
            ids.add(bc.getId());
        }
        message.changeAttachmentIds(ids);
    }


    /**
     * 메시지에 이미 연결된 attachmentIds를 모두 삭제합니다.
     */
    private void deleteExistingAttachments(Message message) {
        List<UUID> oldIds = message.getAttachmentIds();
        if (oldIds != null) {
            for (UUID oldId : oldIds) {
                binaryContentRepository.deleteById(oldId);
            }
        }
    }
}
