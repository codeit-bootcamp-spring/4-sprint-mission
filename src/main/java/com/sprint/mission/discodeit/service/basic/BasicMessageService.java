package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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
    public MessageResponseDto create(MessageCreateDto messageCreateDto) {
        if (!channelRepository.existsById(messageCreateDto.getChannelId())) {
            throw new NoSuchElementException("ID가 " + messageCreateDto.getChannelId() + "인 채널을 찾을 수 없습니다.");
        }
        if (!userRepository.existsById(messageCreateDto.getUserId())) {
            throw new NoSuchElementException("ID가 " + messageCreateDto.getUserId() + "인 작성자를 찾을 수 없습니다.");
        }

        Message message = messageMapper.MessageCreateDtoToMessage(messageCreateDto);

        if (messageCreateDto.isHasFile()) {
            List<BinaryContent> binaryContents = binaryContentMapper.MessageCreateDtoToBinaryContent(messageCreateDto);
            List<UUID> binaryContentIds = binaryContentRepository.saveAll(binaryContents);
            message.setBinaryContentIds(binaryContentIds);
        }
        messageRepository.save(message);

        return messageMapper.MessageToMessageResponseDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다."));
        return messageMapper.MessageToMessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findallByChannelId(UUID channelId) {
        List<Message> channels = messageRepository.findAllByChannelId(channelId);
        return channels.stream()
                .map(messageMapper::MessageToMessageResponseDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(MessageUpdateDto messageUpdateDto) {
        Message message = messageRepository.findById(messageUpdateDto.getMessageId())
                .orElseThrow(() -> new NoSuchElementException("ID가 " + messageUpdateDto.getMessageId() + "인 메시지를 찾을 수 없습니다."));

        message.update(messageUpdateDto.getNewContent());

        BinaryContent content = null;

        if(messageUpdateDto.isHasFile()) {
            if(message.getBinaryIds() != null){
                binaryContentRepository.deleteAllByBinaryIds(message.getBinaryIds());
            }

            List<BinaryContent> binaryContents = binaryContentMapper.MessageUpdateDtoToBinaryContent(messageUpdateDto);
            List<UUID> binaryContentIds = binaryContentRepository.saveAll(binaryContents);
            message.setBinaryContentIds(binaryContentIds);
        }
        messageRepository.save(message);
        return messageMapper.MessageToMessageResponseDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isEmpty()) {
            throw new NoSuchElementException("ID가 " + messageId + "인 메시지를 찾을 수 없습니다.");
        }
        if(message.get().getBinaryIds() != null){
            binaryContentRepository.deleteAllByBinaryIds(message.get().getBinaryIds());
        }
        messageRepository.deleteById(messageId);
    }
}

