package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;
  @InjectMocks
  private BasicMessageService messageService;

  /**
   * 생성 - create
   */
  @DisplayName("메세지 생성 테스트")
  @Test
  void createMessageTest() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID binaryContentId = UUID.randomUUID();

    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Message Content!",
        channelId,
        authorId
    );

    byte[] fileBytes = "Hello World".getBytes();
    List<BinaryContentCreateRequest> binaryRequests = List.of(
        new BinaryContentCreateRequest("test.txt", "text/plain", fileBytes)
    );

    // Mock dependencies
    Channel mockChannel = mock(Channel.class);
    User mockUser = mock(User.class);
    BinaryContent mockSavedBinaryContent = mock(BinaryContent.class);
    Message savedMessage = mock(Message.class);
    MessageDto expectedDto = mock(MessageDto.class);

    // Setup mock behaviors
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));
    when(userRepository.findById(authorId)).thenReturn(Optional.of(mockUser));
    when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(mockSavedBinaryContent);
    when(mockSavedBinaryContent.getId()).thenReturn(binaryContentId);
    when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
    when(messageMapper.toDto(any(Message.class))).thenReturn(expectedDto);

    // =============== when ===============
    MessageDto actualDto = messageService.create(messageCreateRequest, binaryRequests);

    // =============== then ===============
    assertEquals(expectedDto, actualDto);

    verify(channelRepository).findById(channelId);
    verify(userRepository).findById(authorId);
    verify(binaryContentRepository).save(any(BinaryContent.class));
    verify(binaryContentStorage).put(binaryContentId, fileBytes);
    verify(messageRepository).save(any(Message.class));
    verify(messageMapper).toDto(any(Message.class));
  }

  @DisplayName("메세지 생성 테스트 실패 - 채널 존재하지 않음")
  @Test
  void CreateMessage_Failure_Channel_DoesntExist() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Message Content!",
        channelId,
        authorId
    );
    when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

    // =============== when ===============
    assertThrows(ChannelNotFoundException.class,
        () -> messageService.create(messageCreateRequest, List.of()));

    // =============== then ===============
    verify(channelRepository).findById(channelId);
    verify(userRepository, never()).findById(authorId);
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(binaryContentStorage, never()).put(any(UUID.class), any(byte[].class));
    verify(messageRepository, never()).save(any(Message.class));
    verify(messageMapper, never()).toDto(any(Message.class));
  }

  @DisplayName("메세지 생성 테스트 실패 - 유저 존재하지 않음")
  @Test
  void CreateMessage_Failure_User_DoesntExist() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Message Content!",
        channelId,
        authorId
    );
    when(channelRepository.findById(channelId)).thenReturn(
        Optional.of(new Channel(ChannelType.PUBLIC, "test", "test description")));
    when(userRepository.findById(authorId)).thenReturn(Optional.empty());

    // =============== when ===============
    assertThrows(UserNotFoundException.class,
        () -> messageService.create(messageCreateRequest, List.of()));

    // =============== then ===============
    verify(channelRepository).findById(channelId);
    verify(userRepository).findById(authorId);
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(binaryContentStorage, never()).put(any(UUID.class), any(byte[].class));
    verify(messageRepository, never()).save(any(Message.class));
    verify(messageMapper, never()).toDto(any(Message.class));
  }

  /**
   * 수정 - update
   */
  @DisplayName("메시지 수정 테스트")
  @Test
  void updateMessageTest() {
    // ============ given ============
    UUID messageId = UUID.randomUUID();
    Message message = spy(new Message(
        "Content",
        mock(Channel.class),
        mock(User.class),
        null
    ));
    String newContent = "Updated content";
    MessageUpdateRequest request = new MessageUpdateRequest(
        newContent
    );
    MessageDto updatedMessageDto = new MessageDto(
        messageId,
        null,
        null,
        newContent,
        UUID.randomUUID(),
        mock(UserDto.class),
        null
    );
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageMapper.toDto(message)).thenReturn(updatedMessageDto);

    // ============ when ============
    MessageDto returnedMessageDto = messageService.update(messageId, request);

    // ============ then ============
    assertEquals(newContent, returnedMessageDto.content());
    verify(message, times(1)).update(newContent);
    verify(messageRepository, times(1)).findById(messageId);
    verify(messageMapper, times(1)).toDto(message);
  }

  @DisplayName("메시지 수정 테스트 실패 - 메세지 존재하지 않음")
  @Test
  void updateMessage_Failure_MessageDoesNotExist() {
    // =============== given ===============
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated content");
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

    // =============== when ===============
    assertThrows(MessageNotFoundException.class,
        () -> messageService.update(messageId, request));

    // =============== then ===============
    verify(messageRepository).findById(messageId);
    verify(messageMapper, never()).toDto(any(Message.class));
  }

  @DisplayName("메시지 수정 테스트 실패 - 데이터베이스  실패")
  @Test
  void updateMessage_Failure_DBConnection() {
    // ============ given ============
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated content");
    doThrow(new DataAccessException("DB connection failed") {
    })
        .when(messageRepository).findById(any(UUID.class));

    // ============ when ============
    assertThrows(DataAccessException.class,
        () -> messageService.update(messageId, request));

    // ============ then ============
    verify(messageRepository, times(1)).findById(messageId);
    verify(messageMapper, never()).toDto(any(Message.class));
  }

  /**
   * 삭제 - delete
   */
  @DisplayName("메시지 삭제 테스트")
  @Test
  void deleteMessageTest() {
    // =============== given ===============
    UUID messageId = UUID.randomUUID();
    when(messageRepository.existsById(messageId)).thenReturn(true);

    // =============== when ===============
    messageService.delete(messageId);

    // =============== then ===============
    verify(messageRepository, times(1)).existsById(messageId);
    verify(messageRepository, times(1)).deleteById(messageId);
  }

  @DisplayName("메시지 삭제 테스트 실패 - 메세지 존재하지 않음")
  @Test
  void deleteMessage_Failure_MessageDoesNotExist() {
    // =============== given ===============
    UUID messageId = UUID.randomUUID();
    when(messageRepository.existsById(messageId)).thenReturn(false);

    // =============== when ===============
    assertThrows(MessageNotFoundException.class,
        () -> messageService.delete(messageId));

    // =============== then ===============
    verify(messageRepository, never()).deleteById(messageId);
  }


  @DisplayName("메시지 삭제 테스트 실패 - 데이터베이스 실패")
  @Test
  void deleteMessage_Failure_DBConnection() {
    // ============ given ============
    UUID messageId = UUID.randomUUID();
    when(messageRepository.existsById(messageId)).thenReturn(true);
    doThrow(new DataAccessException("DB connection failed") {
    })
        .when(messageRepository).deleteById(any(UUID.class));

    // ============ when ============
    assertThrows(DataAccessException.class,
        () -> messageService.delete(messageId));

    // ============ then ============
    verify(messageRepository, times(1)).existsById(messageId);
    verify(messageRepository, times(1)).deleteById(messageId);
  }

  /**
   * 채널 id로 조회 - findAllByChannelId
   */
  @DisplayName("채널 아이디로 메시지 조회 테스트")
  @Test
  void findAllMessageByChannelIdTest() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    Instant createAt = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    // sample messages from DB
    Message message1 = mock(Message.class);
    Message message2 = mock(Message.class);
    List<Message> messageList = List.of(message1, message2);

    //corresponding DTOs
    MessageDto messageDto1 = new MessageDto(
        UUID.randomUUID(),
        createAt.minusSeconds(5),
        createAt.minusSeconds(5),
        "Content 1",
        channelId,
        mock(UserDto.class),
        null
    );
    MessageDto messageDto2 = new MessageDto(
        UUID.randomUUID(),
        createAt.minusSeconds(10),
        createAt.minusSeconds(10),
        "Content 2",
        channelId,
        mock(UserDto.class),
        null
    );

    // mock what repository returns (Slice of Message objects)
    Slice<Message> messageSlice = new SliceImpl<>(messageList, pageable, true);

    // Create properly typed mock
    PageResponse<MessageDto> mockPageResponse = mock(PageResponse.class);

    // setting up mocks
    when(channelRepository.existsById(channelId)).thenReturn(true);
    when(messageRepository.findAllByChannelIdWithAuthor(channelId, createAt, pageable))
        .thenReturn(messageSlice);
    when(messageMapper.toDto(message1)).thenReturn(messageDto1);
    when(messageMapper.toDto(message2)).thenReturn(messageDto2);
    when(pageResponseMapper.fromSlice(any(Slice.class), any(Instant.class)))
        .thenReturn(mockPageResponse);

    // =============== when ===============
    PageResponse<MessageDto> actualResponse = messageService.findAllByChannelId(channelId, createAt,
        pageable);

    // =============== then ===============
    verify(channelRepository).existsById(channelId);
    verify(messageRepository).findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class),
        any(Pageable.class));
    // 휫수 안해주면 에러뜸
    verify(messageMapper, times(2)).toDto(any(Message.class));
    verify(pageResponseMapper).fromSlice(any(), any(Instant.class));
  }

  @DisplayName("채널 Id로 채널 조회 테스트 실패 - 채널 존재하지 않음")
  @Test
  void findAllMessageByChannelId_Failure_ChannelDoesNotExist() {
    // ============ given ============
    UUID nonExistentChannelId = UUID.randomUUID();
    Instant instant = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);
    when(channelRepository.existsById(nonExistentChannelId)).thenReturn(false);

    // ============ when ============
    assertThrows(ChannelNotFoundException.class,
        () -> messageService.findAllByChannelId(nonExistentChannelId, instant, pageable));

    // ============ then ============
    verify(messageRepository, never()).findAllByChannelIdWithAuthor(any(UUID.class),
        any(Instant.class), any(Pageable.class));
  }

  @DisplayName("채널 Id로 채널 조회 테스트 실패 - 채널에 메세지 없음")
  @Test
  void findAllMessageByChannelId_Failure_NoMessageExists() {
    // ============ given ============
    UUID channelId = UUID.randomUUID();
    Instant instant = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    when(channelRepository.existsById(channelId)).thenReturn(true);
    when(messageRepository.findAllByChannelIdWithAuthor(any(UUID.class), any(Instant.class),
        any(Pageable.class)))
        .thenReturn(new SliceImpl<>(Collections.emptyList(), pageable, false));

    // ============ when ============
    PageResponse<MessageDto> pageResponse = messageService.findAllByChannelId(channelId, instant,
        pageable);

    // ============ then ============
    assertNull(pageResponse, "The content list should be empty.");
    verify(messageRepository).findAllByChannelIdWithAuthor(any(UUID.class),
        any(Instant.class), any(Pageable.class));
  }
}
