package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.ErrorCode;
import com.codeit.discodeit.exception.exception.BusinessException;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.service.MessageService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentService binaryContentService;

  @Override
  @Transactional
  public Message createMessage(Message message, List<byte[]> attachmentsBytes) throws IOException {

    findUserByUserId(message.getAuthor().getId());
    findChannelByChannelId(message.getChannel().getId());
    messageRepository.createMessage(message);

    if (message.getAttachments() != null) {
      for (int i = 0 ; i < message.getAttachments().size(); i++) {
        BinaryContent binaryContent = message.getAttachments().get(i);
        binaryContentService.createByteFile(binaryContent, attachmentsBytes.get(i));
        // 데이터를 바이트 파일로 저장
      }
    }

    return message;
  }

  @Override
  @Transactional
  public void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO) {
    Message message = findMessageByMessageId(deleteMessageRequestDTO.getMessageId());
    messageRepository.deleteMessage(message);
  }

  @Override
  @Transactional
  public Message updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
    Message message = findMessageByMessageId(messageId);
    message.setContent(messageUpdateRequest.getNewContent());

    messageRepository.updateMessage(message);
    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<Message> findMessagesPerPage(UUID channelId, Pageable pageable) {
    List<Message> messageList = messageRepository.findMessagesByChannelId(channelId);


    int page = pageable.getPage();
    int size = pageable.getSize();
    long totalElements = messageList.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    int fromIndex = page * size;
    if (fromIndex >= totalElements) {
      return new PageResponse<>(List.of(), page, size, false, totalElements);
    }

    int toIndex = Math.min(fromIndex + size, messageList.size());
    List<Message> pageContent = messageList.subList(fromIndex, toIndex);
    boolean hasNext = page + 1 < totalPages;

    return new PageResponse<>(pageContent, page, size, hasNext, totalElements);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Message> findLastMessageInChannel(UUID channelId){
    return messageRepository.findLastMessageInChannel(channelId);
  }

  @Override
  @Transactional(readOnly = true)
  public Message findMessageByMessageId(UUID messageId) {
    return messageRepository.findMessageByMessageId(messageId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.NO_FIND_MESSAGE));
  }

  private void findChannelByChannelId(UUID channelId) {
    channelRepository.findChannelByChannelId(channelId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.NO_FIND_CHANNEL));
  }

  private void findUserByUserId(UUID userId) {
    userRepository.findUserByUserId(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NO_FIND_USER));
  }
}