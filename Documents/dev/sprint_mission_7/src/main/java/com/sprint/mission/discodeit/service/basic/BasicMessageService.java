package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.message.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public MessageDto create(MessageCreateDto dto) {
        log.info("메세지 생성 요청: contentLength ={}, attachmentCount={}",
                dto.getContent() !=null ? dto.getContent().length(): 0,
                dto.getAttachmentId() !=null ? dto.getAttachmentId().size(): 0);
        //첨부파일 ID 리스트 유무 확인
        List<BinaryContent> attachments = dto.getAttachmentId().stream()
                .map(id -> binaryContentRepository.findById(id)
                        .orElseThrow(() -> {
                            log.info("메세지 첨부 파일이 존재하지 않음");
                            return new BinaryContentNotFoundException(id);
                        }))
                .toList();

        //파일 데이터 저장
        try {
            for (BinaryContent attachment : attachments) {
                InputStream inputStream = binaryContentStorage.get(attachment.getId());
                byte[] bytes = StreamUtils.copyToByteArray(inputStream);
                binaryContentStorage.put(attachment.getId(),bytes);
            }
        } catch (IOException e) {
            log.error("메세지 생성 실패-첨부 파일 데이터 저장 실패: attachmentId={}", dto.getAttachmentId(), e);
            throw new RuntimeException("파일 정보를 읽어오는 데 실패했습니다.", e);
        }

        //메세지 생성 및 저장
        Message message = messageMapper.messageCreateDtoToMessage(dto);
        message.setAttachments(attachments);
        Message savedMessage = messageRepository.save(message);
        log.info("메세지 생성 완료: messageId={}, attachmentCount={}",
                savedMessage.getId(),
                savedMessage.getAttachments() !=null ? savedMessage.getAttachments().size() : 0);
        return messageMapper.messageToMessageDto(savedMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException( messageId));
        return messageMapper.messageToMessageDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
            return messageRepository.findAllByChannelId(channelId,pageable)
                    .map(messageMapper::messageToMessageDto);

    }

    @Override
    public MessageDto update(MessageUpdateDto dto) {
        log.info("메세지 수정 요청: messageId={}", dto.getMessageId());
        Message message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> {
                    log.error("메세지 수정 실패-해당 메세지 존재하지 않음: messageId={}", dto.getMessageId());
                    return new MessageNotFoundException(dto.getMessageId());
                });

        message.update(dto.getNewContent());
        Message updatedMessage = messageRepository.save(message);
        log.info("메세지 수정 완료: messageId={}, updatedMessage={}", dto.getMessageId(), updatedMessage.getContent());
        return messageMapper.messageToMessageDto(updatedMessage);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        log.info("<메세지 삭제 요청: messageId={}", messageId);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.error("메세지 삭제 실패-해당 메세지 존재하지 않음: messageId={}", messageId);
                    return new MessageNotFoundException(messageId);
                });
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            message.getAttachments().forEach(content -> binaryContentRepository.deleteById(content.getId()));
        }
        messageRepository.deleteById(messageId);
        log.info("메세지 삭제 완료: messageId={}", messageId);
    }
}
