package com.codeit.discodeit.service;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.channel.ChannelNotFoundException;
import com.codeit.discodeit.exception.message.MessageNotFoundException;
import com.codeit.discodeit.exception.user.UserNotFoundException;
import com.codeit.discodeit.mapper.MessageMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.service.basic.BasicMessageService;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private MessageRepository mockMessageRepository;

  @Mock
  private ChannelRepository mockChannelRepository;

  @Mock
  private MessageMapper messageMapper;

  @Mock
  private UserService userService;

  @Mock
  private ChannelService channelService;

  @Mock
  private BinaryContentService binaryContentService;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  void 메시지_생성_성공_테스트() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    user.setUsername("alice");
    user.setEmail("alice@email.com");

    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel();
    channel.setId(channelId);
    channel.setName("general");
    channel.setType(ChannelType.PUBLIC);

    given(userService.findUserByUserId(userId)).willReturn(user);
    given(channelService.findChannelByChannelId(channelId)).willReturn(channel);

    MessageCreateRequest request = new MessageCreateRequest();
    request.setAuthorId(userId);
    request.setChannelId(channelId);
    request.setContent("Hello World!");
    List<BinaryContent> attachmentsList = new ArrayList<>();
    List<MultipartFile> filesList = new ArrayList<>();

    Message mockMessage = new Message();
    mockMessage.setId(UUID.randomUUID());
    mockMessage.setContent(request.getContent());
    mockMessage.setAuthor(user);
    mockMessage.setChannel(channel);

    given(messageMapper.toMessage(request.getContent(), user, channel, attachmentsList))
        .willReturn(mockMessage);

    MessageDto mockDto = new MessageDto(
        mockMessage.getId(),
        mockMessage.getCreatedAt(),
        mockMessage.getUpdatedAt(),
        mockMessage.getContent(),
        mockMessage.getChannel().getId(),
        null,
        null
    );

    given(messageMapper.toMessageDto(mockMessage)).willReturn(mockDto);

    // when
    MessageDto result = messageService.createMessage(request, filesList);

    // then
    then(mockMessageRepository).should().save(mockMessage);
    then(binaryContentService).shouldHaveNoInteractions();
    assertEquals("Hello World!", result.content());
  }

  @Test
  void 유저가_없을때_메시지_생성_실패_테스트() {
    // given
    UUID invalidUserId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest();
    request.setAuthorId(invalidUserId);
    request.setChannelId(UUID.randomUUID());
    request.setContent("Test message");
    List<MultipartFile> attachmentsList = new ArrayList<>();

    given(userService.findUserByUserId(invalidUserId))
        .willThrow(new UserNotFoundException(Map.of("이유", "유저 없음")));

    // when
    Executable action = () -> messageService.createMessage(request, attachmentsList);

    // then
    assertThrows(UserNotFoundException.class, action);
  }

  @Test
  void 채널이_없을때_메시지_생성_실패_테스트() {
    // given
    UUID invalidChannelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest();
    request.setAuthorId(userId);
    request.setChannelId(invalidChannelId);
    request.setContent("Test message");
    List<MultipartFile> attachmentsList = new ArrayList<>();

    User user = new User();
    user.setId(userId);
    user.setUsername("alice");
    user.setEmail("alice@email.com");

    given(userService.findUserByUserId(userId)).willReturn(user);
    given(channelService.findChannelByChannelId(invalidChannelId))
        .willThrow(ChannelNotFoundException.class);

    // when
    Executable action = () -> messageService.createMessage(request, attachmentsList);

    // then
    assertThrows(ChannelNotFoundException.class, action);
  }

  @Test
  void 메시지_업데이트_성공_테스트() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest();
    request.setNewContent("new Test message");

    Message existingMessage = new Message();
    given(mockMessageRepository.findById(messageId))
        .willReturn(Optional.of(existingMessage));

    // when
    messageService.updateMessage(messageId, request);

    // then
    then(mockMessageRepository).should().save(existingMessage);
  }

  @Test
  void 메시지_업데이트_실패_테스트() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest();
    request.setNewContent("new Test message");

    given(mockMessageRepository.findById(messageId))
        .willThrow(MessageNotFoundException.class);

    // when
    Executable action = () -> messageService.updateMessage(messageId, request);

    // then
    assertThrows(MessageNotFoundException.class, action);
  }

  @Test
  void 메시지_삭제_성공_테스트() {
    // given
    UUID messageId = UUID.randomUUID();
    Message message = new Message();
    given(mockMessageRepository.findById(messageId))
        .willReturn(Optional.of(message));

    // when
    messageService.deleteMessage(messageId);

    // then
    then(mockMessageRepository).should().delete(message);
  }

  @Test
  void 메시지_삭제_실패_테스트() {
    // given
    UUID messageId = UUID.randomUUID();
    given(mockMessageRepository.findById(messageId))
        .willReturn(Optional.empty());

    // when
    Executable action = () -> messageService.deleteMessage(messageId);

    // then
    assertThrows(MessageNotFoundException.class, action);
  }

  @Test
  void 채널별_메시지_조회_성공_테스트() {
    // given
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel();
    channel.setId(channelId);
    channel.setName("general");
    channel.setType(ChannelType.PUBLIC);

    given(mockChannelRepository.findById(channelId))
        .willReturn(Optional.of(channel));

    List<Message> messages = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Message msg = new Message();
      msg.setId(UUID.randomUUID());
      msg.setContent("Message " + i);
      msg.setChannel(channel);
      messages.add(msg);
    }

    // messageMapper의 스텁은 한 번만 설정
    given(messageMapper.toMessageDto(any(Message.class)))
        .willAnswer(invocation -> {
          Message m = invocation.getArgument(0);
          return new MessageDto(
              m.getId(),
              m.getCreatedAt(),
              m.getUpdatedAt(),
              m.getContent(),
              m.getChannel().getId(),
              null,
              null
          );
        });

    given(mockMessageRepository.findByChannelId(channelId))
        .willReturn(messages);

    Pageable pageable = new Pageable();
    pageable.setPage(0);
    pageable.setSize(3);

    // when
    PageResponse<MessageDto> response = messageService.findMessagesPerPage(channelId, pageable);

    // then
    assertEquals(3, response.getContent().size());
    assertEquals(0, response.getNumber());
    assertEquals(3, response.getSize());
    assertTrue(response.isHasNext());
    assertEquals(5L, response.getTotalElements());

    then(mockChannelRepository).should().findById(channelId);
    then(mockMessageRepository).should().findByChannelId(channelId);
    messages.subList(0, 3).forEach(msg -> then(messageMapper).should().toMessageDto(msg));
  }


  @Test
  void 채널별_메시지_조회_실패_테스트() {
    // given
    UUID channelId = UUID.randomUUID();
    Pageable pageable = new Pageable();

    given(mockChannelRepository.findById(channelId))
        .willReturn(Optional.empty());

    // when
    Executable action = () -> messageService.findMessagesPerPage(channelId, pageable);

    // then
    assertThrows(ChannelNotFoundException.class, action);
  }
}
