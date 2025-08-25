package com.codeit.discodeit.service;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.dto.channel_service_dto.PublicChannelUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.channel.ChannelNameDuplicateException;
import com.codeit.discodeit.exception.channel.ChannelNotFoundException;
import com.codeit.discodeit.exception.channel.NoParticipantsChannelException;
import com.codeit.discodeit.exception.user.UserNotFoundException;
import com.codeit.discodeit.mapper.ChannelMapper;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.MessageRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.basic.BasicChannelService;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

  @Mock
  private UserRepository mockUserRepository;

  @Mock
  private ChannelRepository mockChannelRepository;

  @Mock
  private MessageRepository mockMessageRepository;

  @Mock
  private ReadStatusService readStatusService;

  @Mock
  private ChannelMapper channelMapper;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicChannelService channelService;


  @Test
  void 공용_채널_생성_성공_테스트() {
    // given
    CreatePublicChannelRequestDto createDto = new CreatePublicChannelRequestDto("testName", "testDescription");

    Channel mockChannel = new Channel();
    mockChannel.setId(UUID.randomUUID());
    mockChannel.setName(createDto.getName());
    mockChannel.setDescription(createDto.getDescription());
    mockChannel.setType(ChannelType.PUBLIC);

    ChannelDto mockDto = new ChannelDto();
    mockDto.setId(UUID.randomUUID());
    mockDto.setName(createDto.getName());
    mockDto.setDescription(createDto.getDescription());

    given(channelMapper.toPublicChannel(any(CreatePublicChannelRequestDto.class))).willReturn(mockChannel);
    given(channelMapper.toChannelDto(any(Channel.class), any(), any(), eq(userMapper))).willReturn(mockDto);
    given(mockUserRepository.findAll()).willReturn(List.of());

    // when
    ChannelDto result = channelService.createPublicChannel(createDto);

    // then
    then(mockChannelRepository).should().save(mockChannel);
    then(mockUserRepository).should().findAll();
    then(readStatusService).should().findReadStatusesByChannelId(any());
  }

  @Test
  void 중복_이름_공용_채널_생성_실패_테스트() {
    // given
    CreatePublicChannelRequestDto createDto = new CreatePublicChannelRequestDto("testName", "testDescription");
    given(mockChannelRepository.findByName(any())).willReturn(Optional.of(new Channel()));

    // when
    Executable action = () -> channelService.createPublicChannel(createDto);

    // then
    assertThrows(ChannelNameDuplicateException.class, action);
  }

  @Test
  void 사적_채널_생성_성공_테스트() {
    // given
    List<UUID> userIdList = List.of(UUID.randomUUID(), UUID.randomUUID());

    Channel mockChannel = new Channel();
    mockChannel.setId(UUID.randomUUID());
    mockChannel.setType(ChannelType.PRIVATE);

    ChannelDto mockDto = new ChannelDto();
    mockDto.setId(UUID.randomUUID());

    given(channelMapper.toPrivateChannel()).willReturn(mockChannel);
    given(channelMapper.toChannelDto(any(Channel.class), any(), any(), eq(userMapper))).willReturn(mockDto);

    // when
    ChannelDto result = channelService.createPrivateChannel(userIdList);

    // then
    then(mockChannelRepository).should().save(mockChannel);
    then(readStatusService).should().findReadStatusesByChannelId(any());
  }

  @Test
  void 사적_채널_생성_실패_테스트() {
    // given
    List<UUID> userIdList = List.of();

    // when
    Executable action = () -> channelService.createPrivateChannel(userIdList);

    // then
    assertThrows(NoParticipantsChannelException.class, action);
  }

  @Test
  void 공용_채널_업데이트_성공_테스트() {
    // given
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("newName", "newDescription");
    UUID channelId = UUID.randomUUID();

    Channel mockChannel = new Channel();
    mockChannel.setId(channelId);
    mockChannel.setName("oldName");
    mockChannel.setDescription("oldDescription");
    mockChannel.setType(ChannelType.PUBLIC);

    given(mockChannelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));
    given(channelMapper.toChannelDto(any(Channel.class), any(), any(), eq(userMapper)))
        .willAnswer(invocation -> {
          Channel c = invocation.getArgument(0);
          ChannelDto dto = new ChannelDto();
          dto.setId(c.getId());
          dto.setName(c.getName());
          dto.setDescription(c.getDescription());
          return dto;
        });

    // when
    ChannelDto result = channelService.updatePublicChannel(channelId, updateRequest);

    // then
    assertEquals(updateRequest.getNewName(), result.getName());
    assertEquals(updateRequest.getNewDescription(), result.getDescription());
    then(mockChannelRepository).should().save(mockChannel);
  }

  @Test
  void 공용_채널_업데이트_실패_테스트() {
    // given
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("newName", "newDescription");
    UUID channelId = UUID.randomUUID();

    // when
    Executable action = () -> channelService.updatePublicChannel(channelId, updateRequest);

    // then
    assertThrows(ChannelNotFoundException.class, action);
  }

  @Test
  void 채널_삭제_성공_테스트() {
    // given
    UUID channelId = UUID.randomUUID();
    Channel mockChannel = new Channel();
    mockChannel.setId(channelId);
    mockChannel.setName("oldName");
    mockChannel.setDescription("oldDescription");
    mockChannel.setType(ChannelType.PUBLIC);

    given(mockChannelRepository.findById(channelId)).willReturn(Optional.of(mockChannel));

    // when
    channelService.deleteChannel(channelId);

    // then
    then(mockChannelRepository).should().delete(mockChannel);
  }

  @Test
  void 채널_삭제_실패_테스트() {
    // given
    UUID channelId = UUID.randomUUID();

    // when
    Executable action = () -> channelService.deleteChannel(channelId);

    // then
    assertThrows(ChannelNotFoundException.class, action);
  }

  @Test
  void 유저별_채널_검색_성공_테스트() {
    // given
    UUID userId = UUID.randomUUID();

    Channel channel1 = new Channel();
    channel1.setId(UUID.randomUUID());
    channel1.setName("channel1");

    Channel channel2 = new Channel();
    channel2.setId(UUID.randomUUID());
    channel2.setName("channel2");

    ReadStatus rs1 = new ReadStatus();
    rs1.setChannel(channel1);
    ReadStatus rs2 = new ReadStatus();
    rs2.setChannel(channel2);

    List<ReadStatus> readStatuses = List.of(rs1, rs2);

    given(readStatusService.findReadStatusesByUserId(userId)).willReturn(readStatuses);
    given(channelMapper.toChannelDto(any(Channel.class), any(), any(), eq(userMapper)))
        .willAnswer(invocation -> {
          Channel c = invocation.getArgument(0);
          return new ChannelDto(
              c.getId(),
              c.getType(),
              c.getName(),
              c.getDescription(),
              new ArrayList<>(), // participants
              null               // lastMessageAt
          );
        });
    given(mockUserRepository.findById(userId)).willReturn(Optional.of(new User()));

    // when
    List<ChannelDto> result = channelService.findChannelListByUserId(userId);

    // then
    then(readStatusService).should().findReadStatusesByUserId(userId);
    then(channelMapper).should(times(2)).toChannelDto(any(Channel.class), any(), any(), eq(userMapper));
  }

  @Test
  void 유저별_채널_검색_실패_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    given(mockUserRepository.findById(userId)).willReturn(Optional.empty());

    // when
    Executable action = () -> channelService.findChannelListByUserId(userId);

    // then
    assertThrows(UserNotFoundException.class, action);
  }
}
