package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
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
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponseDto create(MessageCreateDto dto) {
        if (dto.getAttachmentId() != null) {
            for (UUID attachmentId : dto.getAttachmentId()) {
               if(binaryContentRepository.existsById(attachmentId)) {
                   throw  new NoSuchElementException("첨부파일 아이디 " + attachmentId + " 에 해당하는 파일이 존재하지 않습니다.");
               }
            }
        }
        Message message = new Message(dto.getContent(), dto.getChannelId(), dto.getAuthorId());
        message.setAttachmentId(dto.getAttachmentId());
        Message savedMessage = messageRepository.save(message);
        return messageMapper.messageToMessageResponseDto(savedMessage);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메세지 아이디 " + messageId + " 에 해당하는 메세지를 찾지 못했습니다."));
        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
    List<Message> messages = messageRepository.findAllByChannelId(channelId);
            return messages.stream()
                    .map(messageMapper::messageToMessageResponseDto)
                    .toList();
    }

    @Override
    public MessageResponseDto update(MessageUpdateDto dto) {
        Message message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("메세지 아이디 " + dto.getMessageId() + " 에 해당하는 메세지를 찾지 못했습니다."));

        message.update(dto.getNewContent());
        Message updatedMessage = messageRepository.save(message);
        return messageMapper.messageToMessageResponseDto(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메세지 아이디 " + messageId + " 에 해당하는 메세지를 찾지 못했습니다."));
        if (message.getAttachmentId() != null && !message.getAttachmentId().isEmpty()) {
            message.getAttachmentId().forEach(binaryContentRepository::deleteById);
        }
        messageRepository.deleteById(messageId);
    }
}
