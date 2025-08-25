package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.channel.ChannelNotFoundException;
import com.codeit.discodeit.exception.message.MessageNotFoundException;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.mapper.MessageMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.codeit.discodeit.service.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;

  private final MessageMapper messageMapper;

  private final UserService userService;
  private final ChannelService channelService;
  private final BinaryContentService binaryContentService;

  @Override
  @Transactional
  public MessageDto createMessage(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachmentsList) throws IOException {
    log.info("[createMessage] 메시지 생성 요청: authorId={}, channelId={}, attachmentsCount={}",
        messageCreateRequest.getAuthorId(),
        messageCreateRequest.getChannelId(),
        attachmentsList != null ? attachmentsList.size() : 0);

    User user = userService.findUserByUserId(messageCreateRequest.getAuthorId());
    Channel channel = channelService.findChannelByChannelId(messageCreateRequest.getChannelId());
    List<BinaryContent> binaryContentList = new ArrayList<>();

    if (attachmentsList != null) {
      for (MultipartFile multipartFile : attachmentsList) {
        BinaryContent binaryContent = BinaryContentMapper.attachmentToBinaryContent(multipartFile);
        binaryContentList.add(binaryContent);
        log.info("[createMessage] 첨부파일 처리: fileName={}, size={}",
            multipartFile.getOriginalFilename(), multipartFile.getSize());
      }
    }

    Message message = messageMapper.toMessage(messageCreateRequest.getContent(), user, channel, binaryContentList);
    messageRepository.save(message);

    if (message.getAttachments() != null) {
      for (int i = 0; i < message.getAttachments().size(); i++) {
        BinaryContent binaryContent = message.getAttachments().get(i);
        byte[] bytes = attachmentsList.get(i).getBytes();
        binaryContentService.createByteFile(binaryContent, bytes);
        log.info("[createMessage] 바이트 파일 저장: attachmentName={}, size={}",
            binaryContent.getFileName(), bytes.length);
      }
    }

    MessageDto result = messageMapper.toMessageDto(message);
    log.info("[createMessage] 메시지 생성 완료: messageId={}", result.id());
    return result;
  }

  @Override
  @Transactional
  public void deleteMessage(UUID messageId) {
    log.info("[deleteMessage] 삭제 요청: messageId={}", messageId);
    Message message = findMessageByMessageId(messageId);
    messageRepository.delete(message);
    log.info("[deleteMessage] 메시지 삭제 완료: messageId={}", message.getId());
  }

  @Override
  @Transactional
  public MessageDto updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
    log.info("[updateMessage] 수정 요청: messageId={}, newContent={}", messageId, messageUpdateRequest.getNewContent());
    Message message = findMessageByMessageId(messageId);
    message.setContent(messageUpdateRequest.getNewContent());

    messageRepository.save(message);
    MessageDto result = messageMapper.toMessageDto(message);
    log.info("[updateMessage] 메시지 수정 완료: messageId={}", messageId);
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findMessagesPerPage(UUID channelId, Pageable pageable) {
    if (channelRepository.findById(channelId).isEmpty()) {
      Map<String, Object> details = Map.of("이유", "채널 없음");
      throw new ChannelNotFoundException(details);
    }
    List<Message> messageList = messageRepository.findByChannelId(channelId);

    int page = pageable.getPage();
    int size = pageable.getSize();
    long totalElements = messageList.size();
    int totalPages = (int) Math.ceil((double) totalElements / size);

    int fromIndex = page * size;
    if (fromIndex >= totalElements) {
      return new PageResponse<>(List.of(), page, size, false, totalElements);
    }

    int toIndex = Math.min(fromIndex + size, messageList.size());
    List<MessageDto> pageContent = messageList.subList(fromIndex, toIndex).stream().map(messageMapper::toMessageDto).collect(
        Collectors.toList());
    boolean hasNext = page + 1 < totalPages;

    return new PageResponse<>(pageContent, page, size, hasNext, totalElements);
  }

  @Override
  @Transactional(readOnly = true)
  public Message findMessageByMessageId(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> {
              Map<String, Object> details = Map.of(
                  "이유", "메세지 없음"
              );
              return new MessageNotFoundException(details);
            });
  }
}