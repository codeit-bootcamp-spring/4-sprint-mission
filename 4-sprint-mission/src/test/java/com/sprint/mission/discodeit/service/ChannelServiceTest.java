package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicChannelService channelService;

    @DisplayName("공개 채널을 성공적으로 완료")
    @Test
    void createPublicChannel() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("channelId", "channelName");
        Channel channel = new Channel(request.name(), request.description());
        given(channelRepository.save(channel)).willReturn(channel);
        given(channelRepository.existsByName(anyString())).willReturn(false);

        // when
        Channel createdChannel = channelService.createPublicChannel(request);

        // then
        assertEquals(request.name(), createdChannel.getName());
        assertEquals(request.description(), createdChannel.getDescription());
        assertEquals(ChannelType.PUBLIC, createdChannel.getType());
    }

    @DisplayName("중복된 채널 이름으로 생성 요청 시 ChannelAlreadyExistsException 발생")
    @Test
    void createPublicChannelShouldFailedWhenDuplicateName() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("channelId", "channelName");
        given(channelRepository.existsByName(anyString())).willReturn(true);

        // when & then
        Exception exception = assertThrows(ChannelAlreadyExistsException.class, () -> channelService.createPublicChannel(request));
        assertEquals("Channel Name Already Exists", exception.getMessage());
        assertInstanceOf(ChannelAlreadyExistsException.class, exception);
    }

    @DisplayName("비공개 채널 성공적으로 생성")
    @Test
    void createPrivateChannelOk() {
        // given
        List<UUID> participantIds = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        Channel channel = new Channel();
        given(channelRepository.save(channel)).willReturn(channel);
        given(userRepository.findById(notNull(UUID.class))).willReturn(Optional.of(new User()));
        given(readStatusRepository.save(new ReadStatus(notNull(User.class), channel))).willReturn(new ReadStatus(notNull(User.class), channel));

        // when
        Channel createdChannel = channelService.createPrivateChannel(request);

        // then
        assertEquals(ChannelType.PRIVATE, createdChannel.getType());
        then(userRepository).should(times(2)).findById(notNull(UUID.class));
        then(readStatusRepository).should(times(2)).save(notNull(ReadStatus.class));
    }

    @DisplayName("비공개 채널 생성 시 참여하는 유저를 찾을수 없음")
    @Test
    void createPrivateShouldFailedWhenUserNotFound() {
        // given
        List<UUID> participantIds = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        given(userRepository.findById(notNull(UUID.class))).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(UserNotFoundException.class, () -> channelService.createPrivateChannel(request));
        assertEquals("User Not Found", exception.getMessage());
        assertInstanceOf(UserNotFoundException.class, exception);
    }

    @DisplayName("정상적으로 채널 수정 완료")
    @Test
    void updatePublicChannelShouldReturnUpdatedChannel() {
        // given
        UUID uuid = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDescription");
        Channel channel = Channel.of(uuid, "oldName", "oldDescription", ChannelType.PUBLIC);
        given(channelRepository.save(channel)).willReturn(channel);
        given(channelRepository.findById(uuid)).willReturn(Optional.of(channel));

        // when
        Channel createdChannel = channelService.updateChannel(uuid, request);

        // then
        assertEquals(ChannelType.PUBLIC, createdChannel.getType());
        assertEquals(request.newName(), createdChannel.getName());
        assertEquals(request.newDescription(), createdChannel.getDescription());
        then(channelRepository).should(times(1)).findById(uuid);
        then(channelRepository).should(times(1)).save(channel);
    }

    @DisplayName("비공개 채널은 수정될 수 없음, 비공개 채널 수정 요청 시 PrivateChannelUpdateException 발생")
    @Test
    void updatePrivateUpdateShouldFailedWhenTypeIsPrivate() {
        // given
        UUID uuid = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDescription");
        Channel channel = Channel.of(uuid, "oldName", "oldDescription", ChannelType.PRIVATE);
        given(channelRepository.findById(uuid)).willReturn(Optional.of(channel));

        // when & then
        Exception exception = assertThrows(PrivateChannelUpdateException.class, () -> channelService.updateChannel(uuid, request));
        assertEquals("Private Channel Cannot Be Updated", exception.getMessage());
        assertInstanceOf(PrivateChannelUpdateException.class, exception);
    }

    @DisplayName("수정할 채널을 찾을 수 없음 ChannelNotFoundException 발생")
    @Test
    void updateChannelShouldFailedWhenChannelNotFound() {
        // given
        UUID uuid = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDescription");
        given(channelRepository.findById(uuid)).willReturn(Optional.empty());
        // when & then
        Exception exception = assertThrows(ChannelNotFoundException.class, () -> channelService.updateChannel(uuid, request));
        assertEquals("Channel Not Found", exception.getMessage());
        assertInstanceOf(ChannelNotFoundException.class, exception);
    }

    @DisplayName("정상적으로 채널 삭제 동작")
    @Test
    void deleteChannelOk() {
        // given
        UUID uuid = UUID.randomUUID();
        given(channelRepository.findById(uuid)).willReturn(Optional.of(new Channel()));

        // when
        channelService.deleteChannel(uuid);

        // then
        then(channelRepository).should(times(1)).delete(any(Channel.class));
    }

    @DisplayName("존재하지 않는 채널 삭제 요청 시 ChannelNotFoundException 발생")
    @Test
    void deleteChannelShouldFailedWhenChannelNotFound() {
        // given
        UUID uuid = UUID.randomUUID();
        given(channelRepository.findById(uuid)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(ChannelNotFoundException.class, () -> channelService.deleteChannel(uuid));
        assertEquals("Channel Not Found", exception.getMessage());
        assertInstanceOf(ChannelNotFoundException.class, exception);
    }

    @DisplayName("유저Id로 정상적으로 채널을 찾은 경우")
    @Test
    void channelFindByUserIdOkShouldReturnAllChannel() {
        // given
        UUID userId = UUID.randomUUID();
        Channel publicChannel = Channel.of(UUID.randomUUID(), "name", "description", ChannelType.PUBLIC);
        Channel privateChannel = Channel.of(UUID.randomUUID(), null, null, ChannelType.PRIVATE);
        User user = User.of(userId, "username", "test@email.com", "password", null);
        ReadStatus readStatus = ReadStatus.of(UUID.randomUUID(), user, publicChannel);
        ReadStatus readStatus2 = ReadStatus.of(UUID.randomUUID(), user, privateChannel);
        List<ReadStatus> readStatusList = new ArrayList<>(Arrays.asList(readStatus, readStatus2));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(readStatusRepository.findAllByUser_Id(userId)).willReturn(readStatusList);
        given(channelRepository.findAllByType(ChannelType.PUBLIC)).willReturn(List.of(publicChannel));

        // when
        List<Channel> list = channelService.findAllByUserId(userId);

        // then
        then(userRepository).should(times(1)).findById(userId);
        then(channelRepository).should(times(1)).findAllByType(ChannelType.PUBLIC);
        then(readStatusRepository).should(times(1)).findAllByUser_Id(userId);
        assertEquals(2, list.size());
    }

    @DisplayName("존재하지 않는 유저의 채널을 찾을 경우 UserNotFoundException 발생")
    @Test
    void channelFindByUserIdShouldFailedWhenUserNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(UserNotFoundException.class, () -> channelService.findAllByUserId(userId));
        assertEquals("User Not Found", exception.getMessage());
        assertInstanceOf(UserNotFoundException.class, exception);
    }
}
