package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private static final int PAGE_SIZE = 50;


  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
                           List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    //검증
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("No channel with id " + channelId));

    User author = userRepository.findById(authorId)
            .orElseThrow(() -> new NoSuchElementException("No author with id " + authorId));

    Message message = messageMapper.toEntity(messageCreateRequest, binaryContentCreateRequests);

    message.setChannel(channel);
    message.setAuthor(author);

    message.getAttachments().forEach(binaryContentRepository::save);

    Message save = messageRepository.save(message);

    return messageMapper.toDto(save);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("No message with id " + messageId));

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    List<Message> messages = messageRepository.findAllByChannelId(channelId);

    return messages.stream()
            .map(messageMapper::toDto)
            .toList();
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("No message with id " + messageId));

    messageMapper.updateFromRequest(request, message);

    Message updated = messageRepository.save(message);
    return messageMapper.toDto(updated);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    List<BinaryContent> attachments = message.getAttachments();
    attachments.forEach(binaryContentRepository::delete);

    messageRepository.delete(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> listRecentMessages(UUID channelId, int pageNumber) {
    // 1) PAGE_SIZE(50)개씩, 최근 메시지(createdAt desc) 순으로 Slice 조회
    Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("createdAt").descending());
    Slice<Message> slice = messageRepository.findByChannelId(channelId, pageable);

    // 2) Entity → DTO 매핑
    Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);

    // 3) Slice → PageResponse<T> 변환 (totalElements는 null)
    return pageResponseMapper.fromSlice(dtoSlice);
  }
}
