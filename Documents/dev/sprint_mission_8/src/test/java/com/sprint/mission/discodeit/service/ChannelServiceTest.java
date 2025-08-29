package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ReadStatusMapper readStatusMapper;

    @InjectMocks
    BasicChannelService channelService;

    @Nested
    @DisplayName("createPublic()")
    class createPublicChannel {
        // given
        @Test
        @DisplayName("공개 채널 생성 시 채널명이 중복이면 예외 발생")
        void create_shouldThrow_whenPublicChannelNameDuplicated() {
            //given
            var dto = mock(PublicChannelCreateDto.class);
            when(dto.getName()).thenReturn("dupChannelName");
            when(channelRepository.existsByName("dupChannelName")).thenReturn(true);

            // when
            assertThatThrownBy(()-> channelService.createPublicChannel(dto))
                    .isInstanceOf(ChannelNameAlreadyExistsException.class);

            // then
            verify(channelRepository).existsByName("dupChannelName");
            verify(channelRepository, never()).save(any());
            verify(channelMapper, never()).channelToChannelDto(any());
        }

        @Test
        @DisplayName("공개 채널 생성 시 요청이 유효하면 채널 생성 성공")
        void create_shouldCreatePublicChannel() {
            // given
            var dto = mock(PublicChannelCreateDto.class);
            when(dto.getName()).thenReturn("ChannelName");
            when(channelRepository.existsByName("ChannelName")).thenReturn(false);

            var mapped = new Channel();
            mapped.setName("ChannelName");
            when(channelMapper.publicChannelCreateDtoToChannel(dto)).thenReturn(mapped);

            var savedChannel = new Channel();
            savedChannel.setId(UUID.randomUUID());
            savedChannel.setName("ChannelName");
            when(channelRepository.save(mapped)).thenReturn(savedChannel);

            var expected = new ChannelDto(savedChannel.getId(),
                    ChannelType.PUBLIC,savedChannel.getName(),
                    savedChannel.getDescription(),
                    savedChannel.getUpdatedAt(),
                    List.of());
            when(channelMapper.channelToChannelDto(savedChannel)).thenReturn(expected);

            // when
           var result = channelService.createPublicChannel(dto);

           // then
            assertThat(result).isSameAs(expected);
            verify(channelRepository).existsByName("ChannelName");
            verify(channelMapper).publicChannelCreateDtoToChannel(dto);
            verify(channelRepository).save(mapped);
            verify(channelMapper).channelToChannelDto(savedChannel);
        }
    }

    @Nested
    @DisplayName("createPrivate()")
    class createPrivateChannel {

        @Test
        @DisplayName("비공개 채널 생성 시 채널명이 중복이면 예외 발생")
        void create_shouldThrow_whenPrivateChannelNameDuplicated() {
            // given
            var dto = mock(PrivateChannelCreateDto.class);
            when(dto.getName()).thenReturn("dupChannelName");
            when(channelRepository.existsByName("dupChannelName")).thenReturn(true);

            // when
            assertThatThrownBy(()-> channelService.createPrivateChannel(dto))
                    .isInstanceOf(ChannelNameAlreadyExistsException.class);

            // then
            verify(channelRepository).existsByName("dupChannelName");
            verify(channelRepository, never()).save(any());
            verify(channelMapper, never()).channelToChannelDto(any());
        }

        @Test
        @DisplayName("비공개 채널 생성 시 요청이 유효하면 채널 생성 성공")
        void create_shouldCreatePrivateChannel() {
            // given
            var dto = mock(PrivateChannelCreateDto.class);
            when(dto.getName()).thenReturn("ChannelName");

            //임의의 참가자 두 명 설정
            UUID p1 = UUID.randomUUID();
            UUID p2 = UUID.randomUUID();
            var participantIds = List.of(p1,p2);
            when(dto.getParticipants()).thenReturn(participantIds);
            when(channelRepository.existsByName("ChannelName")).thenReturn(false);

            //매퍼가 채널 엔티티 생성
            var mapped = new Channel();
            mapped.setName("ChannelName");
            mapped.setType(ChannelType.PRIVATE);
            when(channelMapper.privateChannelCreateDtoToChannel(dto)).thenReturn(mapped);

            //참여자 로드
            var u1 = new User(); u1.setId(p1);
            var u2 = new User(); u2.setId(p2);
            when(userRepository.findAllById(participantIds)).thenReturn(List.of(u1, u2));

            var savedChannel = new Channel();
            savedChannel.setId(UUID.randomUUID());
            savedChannel.setName("ChannelName");
            savedChannel.setType(ChannelType.PRIVATE);
            when(channelRepository.save(mapped)).thenReturn(savedChannel);

            //ReadStatus 생성/저장
            var rs1 = mock(ReadStatus.class);
            var rs2 = mock(ReadStatus.class);
            when(readStatusMapper.toEntity(any(User.class),any(Channel.class),any())).thenReturn(rs1,rs2);

            var result1 = mock(UserDto.class);
            var result2 = mock(UserDto.class);
            when(userMapper.userToUserDto(u1)).thenReturn(result1);
            when(userMapper.userToUserDto(u2)).thenReturn(result2);
            var participantsList = List.of(result1,result2);

            //최종 반환
            var expected = new ChannelDto(savedChannel.getId(),
                    ChannelType.PRIVATE,
                    savedChannel.getName(),
                    savedChannel.getDescription(),
                    savedChannel.getUpdatedAt(),
                    participantsList);
            when(channelMapper.channelToChannelDto(savedChannel, participantsList)).thenReturn(expected);

            //when
            var result = channelService.createPrivateChannel(dto);

            //then
            assertThat(result).isSameAs(expected);
            verify(channelRepository).existsByName("ChannelName");
            verify(channelMapper).privateChannelCreateDtoToChannel(dto);
            verify(userRepository).findAllById(participantIds);
            verify(channelRepository).save(mapped);
            verify(readStatusMapper,times(2)).toEntity(any(User.class),any(Channel.class),any());
            verify(readStatusRepository,times(2)).save(any(ReadStatus.class));
            verify(userMapper).userToUserDto(u1);
            verify(userMapper).userToUserDto(u2);
            verify(channelMapper).channelToChannelDto(savedChannel, participantsList);
        }
    }

    @Nested
    @DisplayName("update()")
    class updateChannel {

        @Test
        @DisplayName("채널 정보 수정 시 변경하려는 채널명이 이미 존재하면 예외 발생")
        void update_shouldThrow_whenChannelNameDuplicated() {
            // given
            var dto = mock(ChannelUpdateDto.class);
            when(dto.getNewName()).thenReturn("newChannelName");
            when(channelRepository.existsByName("newChannelName")).thenReturn(true);

            //when
            assertThatThrownBy(() -> channelService.update(dto))
                    .isInstanceOf(ChannelNameAlreadyExistsException.class);

            //then
            verify(channelRepository).existsByName("newChannelName");
            verify(channelRepository, never()).save(any());
            verify(channelMapper, never()).channelToChannelDto(any());
        }

        @Test
        @DisplayName("채널 정보 수정 시 비공개 채널이라면 정보 수정 불가하여 예외 발생")
        void update_shouldTrow_whenPrivateChannel() {
            //given
            UUID channelId = UUID.randomUUID();
            var dto = mock(ChannelUpdateDto.class);
            when(dto.getChannelId()).thenReturn(channelId);
            when(dto.getNewName()).thenReturn("newChannelName");
            when(channelRepository.existsByName("newChannelName")).thenReturn(false);

            Channel privateChannel = new Channel();
            privateChannel.setId(channelId);
            privateChannel.setType(ChannelType.PRIVATE);
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

            //when
            assertThatThrownBy(() -> channelService.update(dto))
                    .isInstanceOf(PrivateChannelUpdateException.class);

            // then
            verify(channelRepository).existsByName("newChannelName");
            verify(channelRepository).findById(channelId);
            verify(channelMapper, never()).channelToChannelDto(any(), any());
        }

        @Test
        @DisplayName("채널 정보 수정 시 요청이 유효하면 채널 정보 수정 성공")
        void update_shouldUpdateChannel() {
            //given
            UUID channelId = UUID.randomUUID();
            var dto = mock(ChannelUpdateDto.class);
            when(dto.getChannelId()).thenReturn(channelId);
            when(dto.getNewName()).thenReturn("newChannelName");
            when(channelRepository.existsByName("newChannelName")).thenReturn(false);

            Channel channel = new Channel();
            channel.setId(channelId);
            channel.setType(ChannelType.PUBLIC);
            channel.setName("oldChannelName");
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

            ChannelDto expected = new ChannelDto(channelId, ChannelType.PUBLIC, "newChannelName", null, null, null);
            when(channelMapper.channelToChannelDto(channel, null)).thenReturn(expected);

            //when
            ChannelDto result = channelService.update(dto);

            //then
            assertThat(result).isSameAs(expected);
            verify(channelRepository).existsByName("newChannelName");
            verify(channelRepository).findById(channelId);
            verify(channelMapper).channelToChannelDto(channel, null);
        }
    }

        @Nested
        @DisplayName("findByUserId()")
        class findChannelByUserId {

            @Test
            @DisplayName("사용자가 속한 비공개 채널은 참여자 목록과 함께 반환")
            void findByUserId_shouldReturnPrivateWithParticipants() {
                //given
                UUID userId = UUID.randomUUID();

                //채널 및 유저 엔티티 구성
                Channel privateChannel = new Channel();
                privateChannel.setId(UUID.randomUUID());
                privateChannel.setType(ChannelType.PRIVATE);
                privateChannel.setName("privateChannelName");

                User pvUser1 = new User();
                pvUser1.setId(UUID.randomUUID());
                User pvUser2 = new User();
                pvUser2.setId(UUID.randomUUID());

                ReadStatus readStatus1 = new ReadStatus();
                readStatus1.setUser(pvUser1);
                readStatus1.setChannel(privateChannel);
                ReadStatus readStatus2 = new ReadStatus();
                readStatus2.setUser(pvUser2);
                readStatus2.setChannel(privateChannel);
                when(readStatusRepository.findAllByUserId(userId)).thenReturn(List.of(readStatus1,readStatus2));

                when(channelRepository.findAllById(List.of(privateChannel.getId()))).thenReturn(List.of(privateChannel));
                when(channelRepository.findAll()).thenReturn(List.of());

                when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
                UserDto dto = mock(UserDto.class);
                when(userMapper.userToUserDto(any(User.class))).thenReturn(dto);

                List<UserDto> participants = List.of(dto,dto);
                ChannelDto expected= new ChannelDto(privateChannel.getId(),ChannelType.PRIVATE,"privateChannelName",null,null,participants);
                when(channelMapper.channelToChannelDto(eq(privateChannel),anyList())).thenReturn(expected);

                //when
                var result = channelService.findAllByUserId(userId);

                //then
                assertThat(result).containsExactly(expected);
                //bug노출 여부 검증: pvUser1,pvUser2의 ID가 아니라, 동일한 userId로 2번 조회되어야 함.
                verify(userRepository,times(2)).findById(eq(userId));

                verify(channelMapper).channelToChannelDto(privateChannel,participants);
            }

            @Test
            @DisplayName("사용자가 속하지 않은 경우(읽음 상태 없음)에는 공개 채널만 반환")
            void findByUser_shouldReturnOnlyPublic_whenNoReadStatuses() {
                // given
                UUID userId = UUID.randomUUID();
                when(readStatusRepository.findAllByUserId(userId)).thenReturn(List.of());

                Channel public1 = new Channel();
                public1.setId(UUID.randomUUID());
                public1.setType(ChannelType.PUBLIC);
                public1.setName("publicChannelName");
                when(channelRepository.findAll()).thenReturn(List.of(public1));
                when(channelRepository.findAllById(List.of())).thenReturn(List.of());

                ChannelDto expected = new ChannelDto(public1.getId(),ChannelType.PUBLIC,"publicChannelName",null,null,null);
                when(channelMapper.channelToChannelDto(public1,null)).thenReturn(expected);

                //when
                var result = channelService.findAllByUserId(userId);

                //then
                assertThat(result).containsExactly(expected);
                verify(readStatusRepository).findAllByUserId(userId);
                verify(channelRepository).findAllById(List.of());
                verify(channelRepository).findAll();
                verify(channelMapper).channelToChannelDto(public1,null);
            }
        }

        @Nested
        @DisplayName("delete()")
        class deleteChannel {

            @Test
            @DisplayName("채널 정보 삭제 시 대상 채널 ID 존재하지 않으면 오류 발생")
            void delete_shouldThrow_whenChannelNotFound() {
                //given
                UUID channelId = UUID.randomUUID();
                when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

                //when & then
                assertThatThrownBy(()-> channelService.delete(channelId))
                        .isInstanceOf(ChannelNotFoundException.class);
                verify(channelRepository).findById(channelId);
                verify(channelRepository, never()).delete(any());
        }

            @Test
            @DisplayName("채널 정보 삭제 시 요청이 유효하면 채널 삭제 성공")
            void delete_shouldDeleteChannel() {
                //given
                UUID channelId = UUID.randomUUID();
                Channel channel = new Channel();
                channel.setId(channelId);
                channel.setType(ChannelType.PRIVATE);
                channel.setName("privateChannelName");
                when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

                //when
                channelService.delete(channelId);

                //then
                verify(channelRepository).findById(channelId);
                verify(channelRepository).deleteById(channelId);
            }

    }
}
