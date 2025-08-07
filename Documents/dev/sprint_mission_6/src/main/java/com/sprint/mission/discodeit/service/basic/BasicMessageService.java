package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public MessageDto create(MessageCreateDto dto) {
        //첨부파일 ID 리스트 유무 확인
        List<BinaryContent> attachments = dto.getAttachmentId().stream()
                .map(id -> binaryContentRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Binary content not found")))
                .toList();

        //파일 데이터 저장
        try {
            for (BinaryContent attachment : attachments) {
                InputStream inputStream = binaryContentStorage.get(attachment.getId());
                byte[] bytes = StreamUtils.copyToByteArray(inputStream);
                binaryContentStorage.put(attachment.getId(),bytes);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("파일 정보를 읽어오는 데 실패했습니다.", e);
        }

        //메세지 생성 및 저장
        Message message = messageMapper.messageCreateDtoToMessage(dto);
        message.setAttachments(attachments);
        Message savedMessage = messageRepository.save(message);
        return messageMapper.messageToMessageDto(savedMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메세지 아이디 " + messageId + " 에 해당하는 메세지를 찾지 못했습니다."));
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
        Message message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("메세지 아이디 " + dto.getMessageId() + " 에 해당하는 메세지를 찾지 못했습니다."));

        message.update(dto.getNewContent());
        Message updatedMessage = messageRepository.save(message);
        return messageMapper.messageToMessageDto(updatedMessage);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 없습니다."));
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            message.getAttachments().forEach(content -> binaryContentRepository.deleteById(content.getId()));
        }
        messageRepository.deleteById(messageId);
    }
}
