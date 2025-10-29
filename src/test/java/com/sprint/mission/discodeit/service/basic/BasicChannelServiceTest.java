package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MockTest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;


public class BasicChannelServiceTest extends MockTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService basicChannelService;

    private Channel publicChannel;
    private Channel privateChannel;
    private User user;

    private UUID publicChannelId;
    private UUID privateChannelId;
    private UUID userId;

    private ChannelResponse publicChannelResponse;
    private ChannelResponse privateChannelResponse;

    private ReadStatus publicReadStatus;
    private ReadStatus privateReadStatus;

    @BeforeEach
    void setUp() {
        publicChannelId = UUID.randomUUID();
        privateChannelId = UUID.randomUUID();
        userId = UUID.randomUUID();

        publicChannel = Channel.builder()
                .name("public")
                .description("public")
                .type(ChannelType.PUBLIC)
                .build();

        privateChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        user = new User("test", "test", "test", null);

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);
        ReflectionTestUtils.setField(privateChannel, "id", privateChannelId);

        publicChannelResponse = new ChannelResponse(publicChannelId, ChannelType.PUBLIC, "public", "public", null,
                null);
        privateChannelResponse = new ChannelResponse(privateChannelId, ChannelType.PRIVATE, "private", "private", null,
                null);

        publicReadStatus = ReadStatus.builder().user(user).channel(publicChannel).build();
        privateReadStatus = ReadStatus.builder().user(user).channel(privateChannel).build();
    }

    @Test
    @DisplayName("Public Channel 생성 성공")
    void createPublicChannelSuccess() {
        //given
        Channel channel = publicChannel;
        ChannelResponse channelResponse = publicChannelResponse;
        ChannelCreateServiceRequest channelCreateServiceRequest = ChannelCreateServiceRequest.builder().name("public")
                .description("public").build();
        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toEntity(any(ChannelCreateServiceRequest.class), any(ChannelType.class))).willReturn(channel);
        given(channelMapper.toResponse(any(Channel.class))).willReturn(channelResponse);

        //when
        ChannelResponse result = basicChannelService.createPublicChannel(channelCreateServiceRequest);

        //then
        assertThat(result).isEqualTo(channelResponse);
        then(channelRepository).should(times(1)).save(any(Channel.class));
    }

    @Test
    @DisplayName("Private Channel 생성 성공")
    void createPrivateChannelSuccess() {
        //given
        Channel channel = privateChannel;
        ChannelResponse channelResponse = privateChannelResponse;
        PrivateChannelCreateServiceRequest channelCreateServiceRequest = PrivateChannelCreateServiceRequest.builder().participantIds(
                List.of(userId)).build();
        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toEntity(any(PrivateChannelCreateServiceRequest.class), any(ChannelType.class))).willReturn(channel);
        given(channelMapper.toResponse(any(Channel.class))).willReturn(channelResponse);
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));

        //when
        ChannelResponse result = basicChannelService.createPrivateChannel(channelCreateServiceRequest);

        //then
        assertThat(result).isEqualTo(channelResponse);
        then(channelRepository).should(times(1)).save(any(Channel.class));
    }

    @Test
    @DisplayName("User Id로 채널들을 조회한다")
    void findChannelsByUserIdSuccess() {
        //given
        List<ReadStatus> readStatuses = List.of(publicReadStatus, privateReadStatus);
        List<Channel> channels = List.of(publicChannel, privateChannel);
        List<ChannelResponse> channelResponses = List.of(publicChannelResponse, publicChannelResponse);
        given(readStatusRepository.findAllByUserId(userId)).willReturn(readStatuses);
        given(channelRepository.findAll()).willReturn(channels);
        given(channelMapper.toResponse(any(Channel.class))).willReturn(publicChannelResponse);

        //when
        List<ChannelResponse> result = basicChannelService.findAllChannelsByUserId(userId);

        //then
        assertThat(result).isEqualTo(channelResponses);
    }

    @Test
    @DisplayName("Public Channel 수정 성공")
    void updatePublicChannelSuccess() {
        //given
        String updateName = "updateName";
        String updateDescription = "updateDescription";
        ChannelUpdateServiceRequest channelUpdateServiceRequest = ChannelUpdateServiceRequest.builder().channelId(
                publicChannelId).name(updateName).description(updateDescription).build();
        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(publicChannel));
        given(channelMapper.toResponse(any(Channel.class))).willReturn(publicChannelResponse);

        //when
        ChannelResponse result = basicChannelService.updateChannel(channelUpdateServiceRequest);

        //then
        assertThat(result).isEqualTo(publicChannelResponse);
    }

    @Test
    @DisplayName("존재하지 않는 채널 수정 시도 시 실패한다")
    void updateChannelFailWhenChannelNotFound() {
        //given
        ChannelUpdateServiceRequest channelUpdateServiceRequest = ChannelUpdateServiceRequest.builder().channelId(
                publicChannelId).name("updateName").description("updateDescription").build();
        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> basicChannelService.updateChannel(channelUpdateServiceRequest))
                .isInstanceOf(ChannelException.class)
                .hasMessage(ChannelErrorCode.CHANNEL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("Private Channel 수정 시도 시 실패한다")
    void updatePrivateChannelFaile() {
        //given
        Channel channel = privateChannel;
        String updateName = "updateName";
        String updateDescription = "updateDescription";
        ChannelUpdateServiceRequest channelUpdateServiceRequest = ChannelUpdateServiceRequest.builder().channelId(
                privateChannelId).name(updateName).description(updateDescription).build();
        given(channelRepository.findById(any(UUID.class))).willReturn(Optional.of(channel));

        //when & then
        assertThatThrownBy(() -> basicChannelService.updateChannel(channelUpdateServiceRequest))
                .isInstanceOf(ChannelException.class)
                .hasMessage(ChannelErrorCode.PRIVATE_CHANNEL_NOT_EDITABLE.getMessage());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannelSuccess() {
        //when
        basicChannelService.deleteChannel(publicChannelId);

        //then
        then(channelRepository).should(times(1)).deleteById(any(UUID.class));
    }
}
