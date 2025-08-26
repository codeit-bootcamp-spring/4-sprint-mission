package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {
  @Mock private MessageRepository messageRepository;

  @Mock private ChannelRepository channelRepository;

  @Mock private UserRepository userRepository;

  @Mock private MessageMapper messageMapper;

  @Mock private BinaryContentStorage binaryContentStorage;

  @Mock private BinaryContentRepository binaryContentRepository;

  @Mock private PageResponseMapper pageResponseMapper;

  @Mock private UserMapper userMapper;

  @InjectMocks private BasicMessageService basicMessageService;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_success() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest("test", channelId, userId);
    List<BinaryContentCreateRequest> binaryRequestList = new ArrayList<>();

    User user = new User();
    Channel channel = new Channel();

    // repository 반환 설정
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(messageRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

    // mapper 반환 설정
    given(messageMapper.toDto(any(Message.class)))
        .willAnswer(
            invocation -> {
              Message msg = invocation.getArgument(0);
              return new MessageDto(
                  msg.getId(),
                  msg.getCreatedAt(),
                  msg.getUpdatedAt(),
                  msg.getContent(),
                  msg.getChannel().getId(),
                  userMapper.toDto(msg.getAuthor()),
                  List.of());
            });

    // when
    MessageDto messageDto = basicMessageService.create(request, binaryRequestList);

    // then
    assertThat(messageDto).isNotNull();

    verify(channelRepository).findById(channelId);
    verify(userRepository).findById(userId);
    verify(messageRepository).save(any());
    verify(messageMapper).toDto(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 채널 없음")
  void create_fail_channelNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("test", channelId, userId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicMessageService.create(request, List.of()))
        .isInstanceOf(MessageNotFoundException.class);

    verify(channelRepository).findById(channelId);
    verify(userRepository, never()).findById(any());
    verify(messageRepository, never()).save(any());
    verify(messageMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("메시지 생성 실패 - 작성자 없음")
  void create_fail_authorNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("test", channelId, userId);

    Channel channel = new Channel();
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicMessageService.create(request, List.of()))
        .isInstanceOf(MessageNotFoundException.class);

    verify(channelRepository).findById(channelId);
    verify(userRepository).findById(userId);
    verify(messageRepository, never()).save(any());
    verify(messageMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("메시지 내용 수정 성공")
  void update_success() {
    UUID messageId = UUID.randomUUID();
    String newContent = "newContent";
    MessageUpdateRequest request = new MessageUpdateRequest(newContent);

    Message message = mock(Message.class);
    Instant now = Instant.now();

    MessageDto messageDto =
        new MessageDto(messageId, now, now, newContent, UUID.randomUUID(), null, List.of());

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(message)).willReturn(messageDto);

    // when
    MessageDto result = basicMessageService.update(messageId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo(newContent);

    verify(messageRepository).findById(messageId);
    verify(message).update(newContent);
    verify(messageMapper).toDto(message);
  }

  @Test
  @DisplayName("메시지 내용 수정 실패 - 메시지를 찾을 수 없음")
  void update_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicMessageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);

    verify(messageRepository).findById(messageId);
    verify(messageMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_success() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(true);

    // when
    basicMessageService.delete(messageId);

    // then
    verify(messageRepository).existsById(messageId);
    verify(messageRepository).deleteById(messageId);
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 메시지 없음")
  void delete_fail_messageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicMessageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);

    verify(messageRepository).existsById(messageId);
    verify(messageRepository, never()).deleteById(any());
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("채널 메시지 조회 성공")
  void findAllByChannelId_success() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant createAt = Instant.now();
    Pageable pageable = Pageable.ofSize(10);

    // 메시지와 DTO 준비
    Message message1 = new Message("content1", null, null, List.of());
    Message message2 = new Message("content2", null, null, List.of());

    MessageDto dto1 =
        new MessageDto(
            message1.getId(),
            message1.getCreatedAt(),
            message1.getUpdatedAt(),
            message1.getContent(),
            null,
            null,
            List.of());
    MessageDto dto2 =
        new MessageDto(
            message2.getId(),
            message2.getCreatedAt(),
            message2.getUpdatedAt(),
            message2.getContent(),
            null,
            null,
            List.of());

    Slice<Message> messageSlice = new SliceImpl<>(List.of(message1, message2), pageable, false);

    // 반환 설정
    given(
            messageRepository.findAllByChannelIdWithAuthor(
                eq(channelId), any(Instant.class), eq(pageable)))
        .willReturn(messageSlice);
    given(messageMapper.toDto(message1)).willReturn(dto1);
    given(messageMapper.toDto(message2)).willReturn(dto2);
    given(pageResponseMapper.fromSlice(any(Slice.class), any()))
        .willAnswer(
            invocation -> {
              Slice<MessageDto> sliceArg = invocation.getArgument(0);
              Instant nextCursorArg = invocation.getArgument(1);
              return new PageResponse<>(
                  sliceArg.getContent(),
                  nextCursorArg,
                  sliceArg.getSize(),
                  sliceArg.hasNext(),
                  (long) sliceArg.getNumberOfElements());
            });

    // when
    PageResponse<MessageDto> response =
        basicMessageService.findAllByChannelId(channelId, createAt, pageable);

    // then
    assertThat(response.content()).hasSize(2);
    assertThat(response.nextCursor()).isEqualTo(dto2.createdAt());
    assertThat(response.size()).isEqualTo(pageable.getPageSize());
    assertThat(response.hasNext()).isFalse();
    assertThat(response.totalElements()).isEqualTo(2L);

    verify(messageRepository)
        .findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable));
    verify(messageMapper).toDto(message1);
    verify(messageMapper).toDto(message2);
    verify(pageResponseMapper).fromSlice(any(Slice.class), any());
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("채널 메시지 조회 - 메시지 없음")
  void findAllByChannelId_empty() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant createAt = Instant.now();
    Pageable pageable = Pageable.ofSize(10);

    // 빈 Slice 반환
    Slice<Message> emptySlice = new SliceImpl<>(List.of(), pageable, false);

    given(
            messageRepository.findAllByChannelIdWithAuthor(
                eq(channelId), any(Instant.class), eq(pageable)))
        .willReturn(emptySlice);

    // pageResponseMapper는 그대로 빈 content 반환
    given(pageResponseMapper.fromSlice(any(Slice.class), any()))
        .willAnswer(
            invocation -> {
              Slice<MessageDto> sliceArg = invocation.getArgument(0);
              Instant nextCursorArg = invocation.getArgument(1);
              return new PageResponse<>(
                  sliceArg.getContent(),
                  nextCursorArg,
                  sliceArg.getSize(),
                  sliceArg.hasNext(),
                  (long) sliceArg.getNumberOfElements());
            });

    // when
    PageResponse<MessageDto> response =
        basicMessageService.findAllByChannelId(channelId, createAt, pageable);

    // then
    assertThat(response.content()).isEmpty();
    assertThat(response.nextCursor()).isNull();
    assertThat(response.size()).isEqualTo(pageable.getPageSize());
    assertThat(response.hasNext()).isFalse();
    assertThat(response.totalElements()).isEqualTo(0L);

    verify(messageRepository)
        .findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable));
    verify(pageResponseMapper).fromSlice(any(Slice.class), any());
  }
}
