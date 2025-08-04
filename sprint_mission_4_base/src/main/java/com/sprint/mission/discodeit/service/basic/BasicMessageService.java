package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.MutilFileCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.maper.BinaryContentMapper;
import com.sprint.mission.discodeit.maper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public MessageResponseDto create(MessageCreateRequest messageCreateRequest, MutilFileCreateRequest mutilFileCreateRequest) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author with id " + authorId + " does not exist");
        }

        List<BinaryContentCreateRequest> binaryContentCreateRequests = multiFileToBinaryContents(mutilFileCreateRequest);
        
        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    BinaryContent binaryContent = binaryContentMapper.binaryContentCreateRequestToBinaryContent(attachmentRequest);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .toList();

        Message message = messageMapper.messageCreateRequestToMessage(messageCreateRequest, attachmentIds);
        messageRepository.save(message);

        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(messageMapper::messageToMessageResponseDto)
                .toList();
    }

    @Override
    public MessageResponseDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        messageRepository.save(message);
        return messageMapper.messageToMessageResponseDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }

    private List<BinaryContentCreateRequest> multiFileToBinaryContents(MutilFileCreateRequest mutilFileCreateRequest) {
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();
        
        if(mutilFileCreateRequest.bytes() != null) {
            for (int i = 0; i < mutilFileCreateRequest.bytes().size(); i++) {
                String fileName = mutilFileCreateRequest.fileName().get(i);
                String contentType = mutilFileCreateRequest.contentType().get(i);
                MultipartFile file = mutilFileCreateRequest.bytes().get(i);

                binaryContentCreateRequests.add(new BinaryContentCreateRequest(fileName, contentType, file));
            }
        }

        return binaryContentCreateRequests;
    }
}
