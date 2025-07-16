package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.dto.message.Message;
import com.sprint.mission.discodeit.dto.message.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BasicBinaryContentService basicBinaryContentService;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public Message createMessage(MessageCreateServiceRequest request) {
    UserEntity author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    ChannelEntity channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

    MessageEntity message = new MessageEntity(request.content(), author, channel);
    message.setToVisible();

    List<MultipartFile> imageFiles = request.image();
    if (imageFiles != null && !imageFiles.isEmpty()) {
      List<UUID> attachmentIds = new ArrayList<>();

      for (MultipartFile imageFile : imageFiles) {
        if (imageFile != null && !imageFile.isEmpty()) {
          BinaryContent created = basicBinaryContentService.createBinaryContent(
              new BinaryContentCreateServiceRequest(message.getId(), null, imageFile));
          attachmentIds.add(created.id());
        }
      }

      message.setAttachmentIds(attachmentIds);
    }

    messageRepository.save(message);
    return messageMapper.messageToMessageResponseDto(message);
  }

  @Override
  public Message updateMessage(UUID messageId, MessageUpdateServiceRequest request) {
    MessageEntity message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException("해당 메시지를 찾을 수 없습니다."));

    boolean updated = false;

    if (request.newContent() != null && !request.newContent().equals(message.getContents())) {
      message.setContents(request.newContent());
      updated = true;
    }

    if (updated) {
      messageRepository.save(message);
    }

    return messageMapper.messageToMessageResponseDto(message);
  }


  @Override
  public void deleteMessage(UUID messageId) {
    MessageEntity message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException("메시지를 찾을 수 없습니다."));

    message.setToDeleted();
    for (UUID attachmentId : message.getAttachmentIds()) {
      binaryContentRepository.delete(attachmentId);
    }
    message.getAttachmentIds().clear();
    messageRepository.save(message);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .filter(m -> m.getState() != MessageState.DELETED)
        .map(messageMapper::messageToMessageResponseDto)
        .collect(Collectors.toList());
  }
}
