package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelArgumentException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {

  @Mock private ChannelRepository channelRepository;

  @Mock private ReadStatusRepository readStatusRepository;

  @Mock private MessageRepository messageRepository;

  @Mock private UserRepository userRepository;

  @Mock private ChannelMapper channelMapper;

  @InjectMocks private BasicChannelService basicChannelService;

  @Test
  @DisplayName("Public 채널 생성 성공")
  void createPublic_success() {
    // given
    // 테스트 데이터
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("test", "testDes");
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "testDes");

    // 반환 설정
    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(channelMapper.toDto(any(Channel.class)))
        .willAnswer(
            invocation -> {
              Channel ch = invocation.getArgument(0);
              return new ChannelDto(
                  ch.getId(), ChannelType.PUBLIC, ch.getName(), ch.getDescription(), null, null);
            });

    // when
    ChannelDto channelDto = basicChannelService.create(request);

    // then
    assertThat(channelDto.name()).isEqualTo("test");
    verify(channelRepository).save(any(Channel.class));
    verify(channelMapper).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("Public 채널 생성 실패 - 채널의 이름이 없으면 채널을 생성할 수 없습니다")
  void createPublic_fail() {
    // given
    // 이름 없는 요청
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(null, "testDes");

    // when & then
    assertThatThrownBy(() -> basicChannelService.create(request))
        .isInstanceOf(InvalidChannelArgumentException.class);

    // 호출되면 안 됨
    verify(channelMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("Private 채널 생성 성공")
  void createPrivate_success() {
    // given
    // 테스트에 필요한 유저 ID와 User 객체 생성
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    User user1 = new User("user1", "user1@email.com", "user1Pass", null);
    User user2 = new User("user2", "user2@email.com", "user2Pass", null);

    // Private 채널 생성 요청 객체 생성
    PrivateChannelCreateRequest request =
        new PrivateChannelCreateRequest(List.of(userId1, userId2));

    // 저장될 채널 객체와 참가자 리스트 및 읽음 상태 준비
    Channel savedChannel = new Channel(ChannelType.PRIVATE, "test", "testDes");
    List<User> participants = List.of(user1, user2);
    List<ReadStatus> readStatuses =
        List.of(
            new ReadStatus(participants.get(0), savedChannel, savedChannel.getCreatedAt()),
            new ReadStatus(participants.get(1), savedChannel, savedChannel.getCreatedAt()));

    // 반환 설정
    given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
    given(userRepository.findAllById(request.participantIds())).willReturn(participants);
    given(readStatusRepository.saveAll(any())).willReturn(readStatuses);
    given(channelMapper.toDto(any(Channel.class)))
        .willAnswer(
            invocation -> {
              Channel ch = invocation.getArgument(0);
              return new ChannelDto(
                  ch.getId(), ch.getType(), ch.getName(), ch.getDescription(), null, null);
            });

    // when
    ChannelDto channelDto = basicChannelService.create(request);

    // then
    assertThat(channelDto.type()).isEqualTo(ChannelType.PRIVATE);
    verify(channelRepository).save(any(Channel.class));
    verify(userRepository).findAllById(request.participantIds());
    verify(readStatusRepository).saveAll(any());
    verify(channelMapper).toDto(any(Channel.class));
  }

  @Test
  @DisplayName("Private 채널 생성 실패 - 참가자가 없으면 채널을 생성할 수 없습니다")
  void createPrivate_fail() {
    // given
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(null);

    // when & then
    assertThatThrownBy(() -> basicChannelService.create(request))
        .isInstanceOf(InvalidChannelArgumentException.class);

    // repository 메서드 호출되지 않아야 함
    verify(channelRepository, never()).save(any());
    verify(userRepository, never()).findAllById(any());
    verify(readStatusRepository, never()).saveAll(any());
    verify(channelMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("Public 채널 정보 수정 성공")
  void updatePublic_success() {
    // given
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PUBLIC, "oldName", "oldDesc");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDesc");
    given(channelMapper.toDto(channel))
        .willAnswer(
            invocation -> {
              Channel ch = invocation.getArgument(0);
              return new ChannelDto(
                  ch.getId(), ch.getType(), ch.getName(), ch.getDescription(), null, null);
            });

    // when
    ChannelDto result = basicChannelService.update(channelId, request);

    // then
    assertThat(result.name()).isEqualTo("newName");
    assertThat(result.description()).isEqualTo("newDesc");
    verify(channelRepository).findById(channelId);
    verify(channelMapper).toDto(channel);
  }

  @Test
  @DisplayName("Private 채널 정보 수정 실패 - Private 채널은 수정 불가")
  void updatePrivate_fail() {
    // given
    UUID channelId = UUID.randomUUID();
    Channel privateChannel = new Channel(ChannelType.PRIVATE, "oldName", "oldDesc");
    given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDesc");

    // when & then
    assertThatThrownBy(() -> basicChannelService.update(channelId, request))
        .isInstanceOf(PrivateChannelUpdateException.class);

    verify(channelMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_success() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    // when
    basicChannelService.delete(channelId);

    // then
    verify(messageRepository).deleteAllByChannelId(channelId);
    verify(readStatusRepository).deleteAllByChannelId(channelId);
    verify(channelRepository).deleteById(channelId);
  }

  @Test
  @DisplayName("채널 삭제 실패 - 채널이 존재하지 않음")
  void delete_fail_notFound() {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicChannelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);

    verify(messageRepository, never()).deleteAllByChannelId(any());
    verify(readStatusRepository, never()).deleteAllByChannelId(any());
    verify(channelRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("findAllByUserId - 구독 채널이 있는 경우")
  void findAllByUserId_success() {
    // given
    UUID userId = UUID.randomUUID();

    // 채널 생성
    Channel channel = new Channel(ChannelType.PUBLIC, "testChannel", "desc");
    ReadStatus readStatus = new ReadStatus(null, channel, null);
    ChannelDto channelDto =
        new ChannelDto(null, ChannelType.PUBLIC, "testChannel", "desc", null, null);

    // 반환 설정
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
        .willReturn(List.of(channel));
    given(channelMapper.toDto(channel)).willReturn(channelDto);

    // when
    List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).type()).isEqualTo(ChannelType.PUBLIC);

    verify(readStatusRepository).findAllByUserId(userId);
    verify(channelRepository).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
    verify(channelMapper).toDto(channel);
  }

  @Test
  @DisplayName("findAllByUserId - 존재하지 않는 userId인 경우")
  void findAllByUserId_userNotFound() {
    UUID nonExistentUserId = UUID.randomUUID();

    // given
    // 해당 userId에 대한 구독 정보 없음
    given(readStatusRepository.findAllByUserId(nonExistentUserId)).willReturn(List.of());

    // when
    List<ChannelDto> result = basicChannelService.findAllByUserId(nonExistentUserId);

    // then
    // 결과는 빈 리스트여야 함
    assertThat(result).isEmpty();

    verify(readStatusRepository).findAllByUserId(nonExistentUserId);
  }
}
