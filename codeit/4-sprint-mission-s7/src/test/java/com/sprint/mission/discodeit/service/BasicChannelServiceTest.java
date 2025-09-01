package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {

    @Mock ChannelRepository channelRepository;
    @Mock ReadStatusRepository readStatusRepository;
    @Mock MessageRepository messageRepository;
    @Mock UserRepository userRepository;
    @Mock ChannelMapper channelMapper;

    @InjectMocks BasicChannelService service;
    
    @Test
    void 공개_채널_생성_성공() {
        //given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개 채널", "모든 유저가 사용 가능한 채널");

        given(channelRepository.save(any(Channel.class)))
                .willReturn(new Channel(ChannelType.PUBLIC, "공개 채널", "모든 유저가 사용 가능한 채널"));

        given(channelMapper.toDto(any(Channel.class))).willAnswer(invocation -> {
            Channel channel = invocation.getArgument(0);
            return new ChannelDto(
                    UUID.randomUUID(),
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription(),
                    Collections.<UserDto>emptyList(),
                    null
            );
        });

        //when
        ChannelDto created = service.create(request);

        //then
        assertThat(created).isNotNull();
        assertThat(created.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(created.name()).isEqualTo("공개 채널");
        assertThat(created.description()).isEqualTo("모든 유저가 사용 가능한 채널");
        assertThat(created.participants()).isEmpty();
        assertThat(created.lastMessageAt()).isNull();

        InOrder inOrder = inOrder(channelRepository, channelMapper);
        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);

        inOrder.verify(channelRepository).save(channelCaptor.capture());
        inOrder.verify(channelMapper).toDto(any(Channel.class));
        inOrder.verifyNoMoreInteractions();

        Channel saved = channelCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(saved.getName()).isEqualTo("공개 채널");
        assertThat(saved.getDescription()).isEqualTo("모든 유저가 사용 가능한 채널");

        then(userRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    void 공개_채널_생성_실패() { //무결성 예외
        //given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개 채널", "모든 유저가 사용 가능한 채널");
        given(channelRepository.save(any(Channel.class)))
                .willThrow(new DataIntegrityViolationException("duplicate channel name"));

        //when + then
        assertThrows(DataIntegrityViolationException.class, () -> service.create(request));
        then(channelRepository).should().save(any(Channel.class));
        then(channelMapper).shouldHaveNoInteractions();
        then(userRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();

    }

    @Test
    void 비공개_채널_생성_성공() {
        //given
        UUID u1Id = UUID.randomUUID();
        UUID u2Id = UUID.randomUUID();
        List<UUID> participantIds = List.of(u1Id, u2Id);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        User u1 = new User("alice", "alice@nate.com", "alice1234", null);
        User u2 = new User("bob", "bob@nate.com", "bob1234", null);

        given(channelRepository.save(any(Channel.class)))
                .willReturn(new Channel(ChannelType.PRIVATE, null, null));

        given(userRepository.findAllById(participantIds))
                .willReturn(List.of(u1, u2));

        given(readStatusRepository.saveAll(anyList()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(channelMapper.toDto(any(Channel.class))).willAnswer(invocation -> {
            Channel channel = invocation.getArgument(0);
            return new ChannelDto(
                    UUID.randomUUID(),
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription(),
                    List.of(),
                    null
            );
        });
        //when
        ChannelDto channelDto = service.create(request);

        //then
        assertThat(channelDto).isNotNull();
        assertThat(channelDto.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(channelDto.name()).isNull();
        assertThat(channelDto.description()).isNull();
        assertThat(channelDto.participants()).isEmpty();
        assertThat(channelDto.lastMessageAt()).isNull();

        InOrder inOrder = inOrder(channelRepository, userRepository, readStatusRepository, channelMapper);
        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ReadStatus>> rsCaptor = ArgumentCaptor.forClass(List.class);

        inOrder.verify(channelRepository).save(channelCaptor.capture());
        inOrder.verify(userRepository).findAllById(eq(participantIds));
        inOrder.verify(readStatusRepository).saveAll(rsCaptor.capture());
        inOrder.verify(channelMapper).toDto(any(Channel.class));
        inOrder.verifyNoMoreInteractions();

        Channel savedChannel = channelCaptor.getValue();
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PRIVATE);
        assertThat(savedChannel.getName()).isNull();
        assertThat(savedChannel.getDescription()).isNull();

        List<ReadStatus> savedReadStatuses = rsCaptor.getValue();
        assertThat(savedReadStatuses).hasSize(2);
        assertThat(savedReadStatuses)
                .allSatisfy(rs -> {
                    assertThat(rs.getUser()).isIn(u1, u2);
                    assertThat(rs.getChannel()).isSameAs(savedChannel);
                });
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    void 비공개_채널_생성_실패() { //participantIds가 null
        //given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(null);

        given(channelRepository.save(any(Channel.class)))
                .willReturn(new Channel(ChannelType.PRIVATE, null, null));

        given(userRepository.findAllById(isNull()))
                .willThrow(new IllegalArgumentException("participantIds must not be null"));

        //when + then
        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAllById(isNull());
        then(readStatusRepository).should(never()).saveAll(anyList());
        then(channelMapper).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    void 채널_수정_성공() {
        //given
        UUID channelId = UUID.randomUUID();
        Channel existing = new Channel(ChannelType.PUBLIC, "공개 채널", "모든 유저가 사용 가능한 채널");

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수다방", "모든 유저가 수다떠는 방");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(existing));

        given(channelMapper.toDto(any(Channel.class))).willAnswer(invocation -> {
            Channel channel = invocation.getArgument(0);
            return new ChannelDto(
                    channelId,
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription(),
                    Collections.<UserDto>emptyList(),
                    null
            );
        });

        //when
        ChannelDto updated = service.update(channelId, request);

        //then
        assertThat(updated).isNotNull();
        assertThat(updated.id()).isEqualTo(channelId);
        assertThat(updated.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(updated.name()).isEqualTo("수다방");
        assertThat(updated.description()).isEqualTo("모든 유저가 수다떠는 방");

        InOrder inOrder = inOrder(channelRepository, channelMapper);
        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);

        inOrder.verify(channelRepository).findById(channelId);
        inOrder.verify(channelMapper).toDto(channelCaptor.capture());
        inOrder.verifyNoMoreInteractions();

        Channel channel = channelCaptor.getValue();
        assertThat(channel.getName()).isEqualTo("수다방");
        assertThat(channel.getDescription()).isEqualTo("모든 유저가 수다떠는 방");

        then(userRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    void 채널_수정_실패() { //채널이 존재하지 않음
        //given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수다방", "모든 유저가 수다떠는 방");
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        //when + then
        assertThrows(ChannelNotFoundException.class, () -> service.update(channelId, request));

        then(channelRepository).should().findById(channelId);
        then(channelMapper).shouldHaveNoInteractions();
        then(userRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    void 비공개_채널_수정_실패() {
        //given
        UUID channelId = UUID.randomUUID();
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("비공개방", "채널명 수정중");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        //when + then
        assertThrows(PrivateChannelUpdateException.class, () -> service.update(channelId, request));

        then(channelRepository).should().findById(channelId);
        then(channelMapper).shouldHaveNoInteractions();
        then(userRepository).shouldHaveNoInteractions();
        then(readStatusRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    @Test
    void 채널_삭제_성공() {
        //given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(true);

        //when
        service.delete(channelId);
        
        //then
        InOrder inOrder = inOrder(channelRepository, messageRepository, readStatusRepository);
        inOrder.verify(channelRepository).existsById(channelId);
        inOrder.verify(messageRepository).deleteAllByChannelId(channelId);
        inOrder.verify(readStatusRepository).deleteAllByChannelId(channelId);
        inOrder.verify(channelRepository).deleteById(channelId);
        inOrder.verifyNoMoreInteractions();

        // 다른 의존성은 관여하지 않음
        then(userRepository).shouldHaveNoInteractions();
        then(channelMapper).shouldHaveNoInteractions();
    }

    @Test
    void 채널_삭제_실패() { //채널이 존재하지 않음
        //given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(false);

        //when + then
        assertThrows(ChannelNotFoundException.class, () -> service.delete(channelId));

        then(channelRepository).should().existsById(channelId);
        then(messageRepository).should(never()).deleteAllByChannelId(any());
        then(readStatusRepository).should(never()).deleteAllByChannelId(any());
        then(channelRepository).should(never()).deleteById(any());

        // 다른 의존성은 관여하지 않음
        then(userRepository).shouldHaveNoInteractions();
        then(channelMapper).shouldHaveNoInteractions();
    }
    
    @Test
    void 유저아이디로_채널_전부_조회_성공() {
        //given
        UUID userId = UUID.randomUUID();

        User user = new User("alice", "alice@nate.com", "alice1234", null);
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        ReadStatus readStatus = new ReadStatus(user, channel, /*lastReadAt*/ null);

        given(readStatusRepository.findAllByUserId(userId))
                .willReturn(List.of(readStatus));

        Channel public1  = new Channel(ChannelType.PUBLIC,  "공개방", "모든 참가자가 볼 수 있음");
        Channel private1 = new Channel(ChannelType.PRIVATE, null, null);

        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
                .willReturn(List.of(public1, private1));

        given(channelMapper.toDto(public1)).willReturn(new ChannelDto(
                UUID.randomUUID(), ChannelType.PUBLIC, "공개방", "모든 참가자가 볼 수 있음",
                Collections.<UserDto>emptyList(), null
        ));
        given(channelMapper.toDto(private1)).willReturn(new ChannelDto(
                UUID.randomUUID(), ChannelType.PRIVATE, null, null,
                Collections.<UserDto>emptyList(), null
        ));

        //when
        List<ChannelDto> result = service.findAllByUserId(userId);
        
        //then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ChannelDto::type)
                .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PRIVATE);
        assertThat(result).extracting(ChannelDto::name)
                .contains("공개방", (String) null);

        then(readStatusRepository).should().findAllByUserId(userId);
        then(channelRepository).should().findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
        then(channelMapper).should(times(2)).toDto(any(Channel.class));

        // 기타 리포지토리는 관여하지 않음
        then(userRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }
    
    @Test
    void 유저아이디로_채널_전부_조회_실패() { //Repository 오류
        //given
        UUID userId = UUID.randomUUID();

        given(readStatusRepository.findAllByUserId(userId)).willReturn(Collections.emptyList());

        RuntimeException boom = new RuntimeException("DB error");
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
                .willThrow(boom);

        //when + then
        assertThrows(RuntimeException.class, () -> service.findAllByUserId(userId));

        then(readStatusRepository).should().findAllByUserId(userId);
        then(channelRepository).should().findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
        then(channelMapper).shouldHaveNoInteractions();

        then(userRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }
}
