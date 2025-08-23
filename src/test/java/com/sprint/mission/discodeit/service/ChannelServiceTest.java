package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @InjectMocks
    private BasicChannelService channelService;

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;

    @Test
    @DisplayName("public channel 생성 성공")
    void public_channel_create_success() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "name",
                "description"
        );
        Channel savedChannel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

        ChannelDto expect = new ChannelDto(
                savedChannel.getId(),
                ChannelType.PUBLIC,
                request.name(),
                request.description(),
                null,
                null
        );
        given(channelMapper.toDto(any(Channel.class))).willReturn(expect);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertAll(
                "public channel 생성 검중",
                () -> assertEquals(expect.id(), result.id()),
                () -> assertEquals(expect.type(), result.type()),
                () -> assertEquals(expect.name(), result.name()),
                () -> assertEquals(expect.description(), result.description()),
                () -> assertEquals(expect.participants(), result.participants()),
                () -> assertEquals(expect.lastMessageAt(), result.lastMessageAt())
        );
        then(channelRepository).should(times(1)).save(any(Channel.class));
        then(channelMapper).should(times(1)).toDto(any(Channel.class));
    }

    @Test
    @DisplayName("private 채널 생성 성공")
    void private_channel_create_success() {
        // given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );
        Channel savedChannel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );
        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

        User user1 = new User("user1", "user1@codeit.com", "password", null);
        User user2 = new User("user2", "user2@codeit.com", "password", null);
        given(userRepository.findAllById(request.participantIds())).willReturn(List.of(user1, user2));
        given(readStatusRepository.saveAll(anyList())).willReturn(null);

        List<UserDto> expectParticipants = List.of(
                new UserDto(user1.getId(), user1.getUsername(), user1.getEmail(), null, true),
                new UserDto(user2.getId(), user2.getUsername(), user2.getEmail(), null, true)
        );
        ChannelDto expect = new ChannelDto(
                savedChannel.getId(),
                savedChannel.getType(),
                savedChannel.getName(),
                savedChannel.getDescription(),
                expectParticipants,
                null
        );
        given(channelMapper.toDto(any(Channel.class))).willReturn(expect);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertAll(
                "private channel 생성 검증",
                () -> assertEquals(expect.id(), result.id()),
                () -> assertEquals(expect.type(), result.type()),
                () -> assertEquals(expect.name(), result.name()),
                () -> assertEquals(expect.description(), result.description()),
                () -> assertEquals(expect.participants(), result.participants()),
                () -> assertEquals(expect.lastMessageAt(), result.lastMessageAt())
        );
        then(channelRepository).should(times(1)).save(any(Channel.class));
        then(readStatusRepository).should(times(1)).saveAll(anyList());
        then(userRepository).should(times(1)).findAllById(request.participantIds());
        then(channelMapper).should(times(1)).toDto(any(Channel.class));
    }

    @Test
    @DisplayName("public channel 수정 성공")
    void update() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "newName",
                "newDescription"
        );
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                "name",
                "description"
        );
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        ChannelDto expect = new ChannelDto(
                channel.getId(),
                ChannelType.PUBLIC,
                request.newName(),
                request.newDescription(),
                null,
                null
        );
        given(channelMapper.toDto(any(Channel.class))).willReturn(expect);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertAll(
                "public 채널 업데이트 성공",
                () -> assertEquals(expect.id(), result.id()),
                () -> assertEquals(expect.type(), result.type()),
                () -> assertEquals(expect.name(), result.name()),
                () -> assertEquals(expect.description(), result.description()),
                () -> assertEquals(expect.participants(), result.participants()),
                () -> assertEquals(expect.lastMessageAt(), result.lastMessageAt())
        );
        then(channelRepository).should(times(1)).findById(channelId);
        then(channelMapper).should(times(1)).toDto(any(Channel.class));
    }

    @Test
    @DisplayName("channel 삭제 성공")
    void delete() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(true);

        willDoNothing().given(messageRepository).deleteAllByChannelId(channelId);
        willDoNothing().given(readStatusRepository).deleteAllByChannelId(channelId);

        // when
        channelService.delete(channelId);

        // then
        then(channelRepository).should(times(1)).existsById(channelId);
        then(channelRepository).should(times(1)).deleteById(channelId);
        then(messageRepository).should(times(1)).deleteAllByChannelId(channelId);
        then(readStatusRepository).should(times(1)).deleteAllByChannelId(channelId);
    }

    @Test
    @DisplayName("public channel 생성 시 DB 저장 실패로 예외 발생")
    void should_throw_runtime_exception_when_db_save_fail_on_public_channel_create() {
        //given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "name",
                "description"
        );
        given(channelRepository.save(any(Channel.class))).willThrow(new RuntimeException("DB 저장 실패"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> channelService.create(request));
        assertEquals("DB 저장 실패", exception.getMessage());
        then(channelRepository).should(times(1)).save(any(Channel.class));
    }

    @Test
    @DisplayName("private channel 생성 시 사용자 조회 실패로 예외 발생")
    void should_throw_user_not_found_exception_when_private_channel_create() {
        // given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );
        Channel savedChannel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );
        given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);

        given(userRepository.findAllById(request.participantIds()))
                .willThrow(new UserNotFoundException(Map.of("participantIds", request.participantIds())));

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> channelService.create(request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        then(channelRepository).should(times(1)).save(any(Channel.class));
        then(userRepository).should(times(1)).findAllById(request.participantIds());
    }

    @Test
    @DisplayName("public channel 수정 시 채널 미존재로 예외 발생")
    void should_throw_channel_not_found_exception_when_channel_update() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "newName",
                "newDescription"
        );
        given(channelRepository.findById(channelId))
                .willThrow(new ChannelNotFoundException(Map.of("channelId", channelId)));

        // when & then
        ChannelNotFoundException exception = assertThrows(ChannelNotFoundException.class, () -> channelService.update(channelId, request));
        assertEquals(ErrorCode.CHANNEL_NOT_FOUND, exception.getErrorCode());
        then(channelRepository).should(times(1)).findById(channelId);
    }

    @Test
    @DisplayName("channel 삭제 시 channel 미존재로 예외 발생")
    void should_throw_channel_not_found_exception_when_channel_delete() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId))
                .willThrow(new ChannelNotFoundException(Map.of("channelId", channelId)));

        // when & then
        ChannelNotFoundException exception = assertThrows(ChannelNotFoundException.class, () -> channelService.delete(channelId));
        assertEquals(ErrorCode.CHANNEL_NOT_FOUND, exception.getErrorCode());
        then(channelRepository).should(times(1)).existsById(channelId);
    }
}