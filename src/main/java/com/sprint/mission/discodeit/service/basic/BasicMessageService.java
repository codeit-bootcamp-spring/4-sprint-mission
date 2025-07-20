package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public MessageResponseDto create(MessageCreateDto createMessageDto, List<MultipartFile> attachments) {
        validateChannelExists(createMessageDto.getChannelId());
        validateAuthorExists(createMessageDto.getAuthorId());

        Message message = messageMapper.toEntity(createMessageDto);
        try {
            handleAttachments(message, attachments, BinaryContentType.MESSAGE);
        } catch (IOException e) {
            // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }

        messageRepository.save(message);
        return messageMapper.toDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = requireMessage(messageId);
        return messageMapper.toDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        List<Message> messageList = messageRepository.findAllByChannelId(channelId);
        return messageMapper.toDtoList(messageList);
    }

    @Override
    public MessageResponseDto update(UUID messageId, MessageUpdateDto updateMessageDto) {
        Message message = requireMessage(messageId);
        List<MultipartFile> files = updateMessageDto.getAttachments();
        BinaryContentType type = updateMessageDto.getAttachmentType();
        if (files != null && !files.isEmpty() && type != null) {
            deleteExistingAttachments(message);
            try {
                handleAttachments(message, files, type);
            } catch (IOException e) {
                // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
                throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
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

    /**
     * MultipartFile 목록과 타입을 받아 아래 과정을 수행합니다.
     * 1) mapper.toEntitys() 로 BinaryContent 리스트 생성
     * 2) saveAll()로 배치 저장
     * 3) 생성된 ID 목록을 message 에 설정
     *
     * @param message        메시지 객체
     * @param attachments    파일 리스트
     * @param attachmentType 파일 유형(MESSAGE)
     * @return 메시지 객체의 attachmentIds 리스트를 반환
     */
    private void handleAttachments(Message message,
                                   List<MultipartFile> attachments,
                                   BinaryContentType attachmentType) throws IOException {
        if (attachments == null || attachments.isEmpty() || attachmentType == null) {
            return;
        }
        List<BinaryContent> entities = binaryContentMapper.toEntities(attachments, attachmentType);
        List<BinaryContent> saved = binaryContentRepository.saveAll(entities);
        List<UUID> ids = saved.stream()
                .map(BinaryContent::getId)
                .toList();
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
