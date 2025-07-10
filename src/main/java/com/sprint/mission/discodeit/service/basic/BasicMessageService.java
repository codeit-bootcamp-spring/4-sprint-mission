package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.dto.MessageDto.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;

  @Override
  public MessageResponse create(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) {

    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel not found with id " + channelId);
    }
    if (!userRepository.existsById(authorId)) {
      throw new NoSuchElementException("Author not found with id " + authorId);
    }

    List<UUID> attachmentIds = null;
    if (attachments != null) {

      attachmentIds = attachments.stream()
          .map(attachmentRequest -> {
            String fileName = attachmentRequest.getOriginalFilename();
            String contentType = attachmentRequest.getContentType();
            byte[] bytes = null;
            try {
              bytes = attachmentRequest.getBytes();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }

            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                contentType, bytes);
            BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
            return createdBinaryContent.getId();
          })
          .toList();
    }

    String content = messageCreateRequest.content();
    Message message = new Message(
        content,
        channelId,
        authorId,
        attachmentIds
    );

    Message savedMessage = messageRepository.save(message);

    return messageMapper.toMessageResponseDto(savedMessage);
  }

  @Override
  public MessageResponse find(UUID messageId) {
    Message msg = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    return messageMapper.toMessageResponseDto(msg);
  }

  @Override
  public List<MessageResponse> findallByChannelId(UUID channelId) {

    return messageRepository.findByChannelId(channelId)
        .stream()
        .map(messageMapper::toMessageResponseDto)
        .toList();
  }

  @Override
  public MessageResponse update(UUID messageId,
      MessageUpdateRequestDto messageUpdateRequestDto) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.update(messageUpdateRequestDto.newContent());
    return messageMapper.toMessageResponseDto(messageRepository.save(message));
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    // deleting binary contents attached to the message
    message.getAttachmentIds()
        .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(messageId);

  }

  @Override
  public void deleteAll() {
    messageRepository.deleteAll();
  }
}
