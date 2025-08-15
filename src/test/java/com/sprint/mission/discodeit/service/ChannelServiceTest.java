package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.*;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  /**
   * 생성 - create(PUBLIC, PRIVATE)
   */

  @DisplayName("공개 채널 생성 테스트")
  @Test
  void createPublicChannelTest() {
    // =============== given ===============
    PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
        "Test Public Channel",
        "This is a test public channel."
    );
    UUID channelId = UUID.randomUUID();
    ChannelDto expectedDto = new ChannelDto(channelId,
        ChannelType.PUBLIC,
        "Test Public Channel",
        "This is a test public channel",
        null,
        null
    );
    when(channelRepository.save(any(Channel.class))).thenReturn(new Channel(ChannelType.PUBLIC,
        "Test Public Channel",
        "This is a test public channel."
    ));
    when(channelMapper.toDto(any(Channel.class))).thenReturn(expectedDto);

    // =============== when ===============
    ChannelDto actualDto = channelService.create(publicChannelCreateRequest);

    // =============== then ===============
    assertNotNull(actualDto);
    assertEquals(ChannelType.PUBLIC, actualDto.type());
    assertEquals(expectedDto.name(), actualDto.name());
    assertEquals(expectedDto.description(), actualDto.description());
    verify(channelRepository).save(any(Channel.class)); // save method called?
    verify(channelMapper).toDto(any(Channel.class)); // mapper called with the correct object?
  }

  @DisplayName("공개 채널 생성 테스트 실패 - 이름 중복")
  @Test
  void createPublicChannel_Failure_DuplicateName() {
    // ============ given ============
    PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
        "Duplicated name",
        "This is a test public channel."
    );
    when(channelRepository.existsByName("Duplicated name")).thenReturn(true);

    // ============ when ============
    assertThrows(ChannelWithNameAlreadyExistsException.class,
        () -> channelService.create(publicChannelCreateRequest));

    // ============ then ============
    verify(channelRepository, never()).save(any(Channel.class));
  }

  @DisplayName("공개 채널 생성 테스트 실패 - 데이터베이스 저장 실패")
  @Test
  void createPublicChannel_Failure_DBConnection() {
    // ============ given ============
    PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(
        "Duplicated name",
        "This is a test public channel."
    );
    when(channelRepository.save(any(Channel.class)))
        .thenThrow(new DataAccessException("DB connection failed") {
        });

    // ============ when ============
    assertThrows(DataAccessException.class,
        () -> channelService.create(publicChannelCreateRequest));

    // ============ then ============
    verify(channelRepository, times(1)).save(any(Channel.class));
    verify(channelMapper, never()).toDto(any(Channel.class));
  }

  @DisplayName("비공개 채널 생성 테스트")
  @Test
  void createPrivateChannelTest() {
    // =============== given ===============
    UUID user1Id = UUID.randomUUID();
    UUID user2Id = UUID.randomUUID();
    List<UUID> participantIdList = List.of(user1Id, user2Id);
    PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
        participantIdList
    );
    User user1 = new User("Bob", "bob@gmail.com", "1234", null);
    User user2 = new User("Alice", "alice@gmail.com", "1234", null);
    ReflectionTestUtils.setField(user1, "id", user1Id);
    ReflectionTestUtils.setField(user2, "id", user2Id);
    List<User> foundUsers = List.of(user1, user2);

    UserDto userDto1 = new UserDto(user1Id, "Bob", "bob@gmail.com", null, true);
    UserDto userDto2 = new UserDto(user2Id, "Alice", "alice@gmail.com", null, true);
    List<UserDto> expectedUsers = List.of(userDto1, userDto2);

    ChannelDto expectedDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        null,
        null,
        expectedUsers,
        null
    );

    when(userRepository.findAllById(participantIdList)).thenReturn(foundUsers);
    when(channelRepository.save(any(Channel.class))).thenReturn(
        new Channel(ChannelType.PRIVATE, null, null));
    when(readStatusRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
    when(channelMapper.toDto(any(Channel.class))).thenReturn(expectedDto);

    // =============== when ===============
    ChannelDto actualDto = channelService.create(privateChannelCreateRequest);

    // =============== then ===============
    verify(userRepository).findAllById(participantIdList);
    verify(channelRepository).save(any(Channel.class)); // save method called?
    verify(readStatusRepository).saveAll(anyList());
    assertEquals(ChannelType.PRIVATE, actualDto.type());
    assertEquals(2, actualDto.participants().size());
    assertNull(actualDto.name(), "Private channels should not have a name.");
    assertNull(actualDto.description(), "Private channels should not have a description.");
  }

  @DisplayName("비공개  채널 생성 테스트 실패 - 유저 조회 실패")
  @Test
  void createPrivateChannel_Failure_UserDoesntExist() {
    // ============ given ============
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    List<UUID> participantsIds = List.of(userId1, userId2);
    PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
        participantsIds
    );
    when(userRepository.findAllById(participantsIds))
        .thenThrow(new UserNotFoundException(userId1));

    // ============ when ============
    assertThrows(UserNotFoundException.class,
        () -> channelService.create(privateChannelCreateRequest));

    // ============ then ============
    verify(channelRepository).save(any(Channel.class));
    verify(readStatusRepository, never()).save(any(ReadStatus.class));
    verify(channelMapper, never()).toDto(any(Channel.class));
  }

  @DisplayName("비공개 채널 생성 테스트 실패 - 데이터베이스 저장 실패")
  @Test
  void createPrivateChannel_Failure_DBConnection() {
    // ============ given ============
    PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID())
    );
    when(channelRepository.save(any(Channel.class)))
        .thenThrow(new DataAccessException("DB connection failed") {
        });

    // ============ when ============
    assertThrows(DataAccessException.class,
        () -> channelService.create(privateChannelCreateRequest));

    // ============ then ============
    verify(channelRepository).save(any(Channel.class));
    verify(userRepository, never()).findAllById(anyList());
    verify(readStatusRepository, never()).save(any(ReadStatus.class));
    verify(channelMapper, never()).toDto(any(Channel.class));
  }

  /**
   * 수정 - update
   */
  @DisplayName("채널 수정 테스트")
  @Test
  void updateChannelTest() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();

    String newName = "New test Public Channel";
    String newDescription = "This is a new description of test public channel.";
    Channel channel = spy(new Channel(
        ChannelType.PUBLIC,
        "Old name",
        "Old description"
    ));
    PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );
    ChannelDto expectedUpdatedChannelDto = new ChannelDto(
        channelId,
        ChannelType.PUBLIC,
        newName,
        newDescription,
        null,
        null
    );

    when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(channelMapper.toDto(any(Channel.class))).thenReturn(expectedUpdatedChannelDto);

    // =============== when ===============
    ChannelDto updatedChannelDto = channelService.update(channelId, publicChannelUpdateRequest);

    // =============== then ===============
    assertEquals(expectedUpdatedChannelDto, updatedChannelDto);
    assertEquals(newName, updatedChannelDto.name());
    assertEquals(newDescription, updatedChannelDto.description());
    verify(channel).update(newName, newDescription);
    // can pass channel because object reference after update stays the same
    verify(channelMapper).toDto(channel);
  }

  @DisplayName("채널 수정 테스트 실패 - 비공개 채널 수정")
  @Test
  void updateChannel_Failure_PrivateChannel() {
    // ============ given ============
    PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest(
        "Test Public Channel",
        "This is a test public channel."
    );
    UUID channelId = UUID.randomUUID();
    Channel mockChannel = mock(Channel.class);
    when(mockChannel.getType()).thenReturn(ChannelType.PRIVATE);
    when(mockChannel.getId()).thenReturn(channelId);
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));

    // ============ when ============
    assertThrows(ChannelUpdatePrivateChannelException.class,
        () -> channelService.update(channelId, publicChannelUpdateRequest));

    // ============ then ============
    verify(mockChannel, never()).update(anyString(), anyString());
    verify(channelMapper, never()).toDto(any(Channel.class));
  }

  @DisplayName("채널 수정 테스트 실패 - 채널 존재하지 않음")
  @Test
  void updateChannel_Failure_TestChannelNotFound() {
    // ============ given ============
    PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest(
        "Test Public Channel",
        "This is a test public channel."
    );
    UUID channelId = UUID.randomUUID();
    when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

    // ============ when ============
    assertThrows(ChannelNotFoundException.class,
        () -> channelService.update(channelId, publicChannelUpdateRequest));

    // ============ then ============
    verify(channelRepository, never()).save(any(Channel.class));
    verify(readStatusRepository, never()).save(any(ReadStatus.class));
    verify(channelMapper, never()).toDto(any(Channel.class));
  }

  /**
   * 삭제 - delete
   */
  @DisplayName("채널 삭제 테스트")
  @Test
  void deleteChannelTest() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    when(channelRepository.existsById(channelId)).thenReturn(true);

    // =============== when ===============
    channelService.delete(channelId);

    // =============== then ===============
    verify(channelRepository).existsById(channelId);
    verify(messageRepository).deleteAllByChannelId(channelId);
    verify(readStatusRepository).deleteAllByChannelId(channelId);
    verify(channelRepository).deleteById(channelId);
  }

  @DisplayName("채널 삭제 테스트 실패 - 채널 존재하지 않음")
  @Test
  void deleteChannel_Failure_Channel_DoesntExist() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    when(channelRepository.existsById(channelId)).thenReturn(false);

    // =============== when ===============
    assertThrows(ChannelNotFoundException.class,
        () -> channelService.delete(channelId));

    // =============== then ===============
    verify(channelRepository, times(1)).existsById(channelId);
    verify(messageRepository, never()).deleteAllByChannelId(channelId);
    verify(readStatusRepository, never()).deleteAllByChannelId(channelId);
    verify(channelRepository, never()).deleteById(channelId);
  }

  @DisplayName("채널 삭제 테스트 실패 - 의존성 에러로 인한 롤백")
  // 의존성 에러로 인한 DB 에러
  @Test
  void deleteChannel_Failure_RollbackDependency() {
    // =============== given ===============
    UUID channelId = UUID.randomUUID();
    when(channelRepository.existsById(channelId)).thenReturn(true);
    doThrow(new DataAccessException("DB connection lost") {
    })
        .when(readStatusRepository).deleteAllByChannelId(channelId);

    // =============== when ===============
    assertThrows(DataAccessException.class,
        () -> channelService.delete(channelId));

    // =============== then ===============
    verify(channelRepository, times(1)).existsById(channelId);
    verify(messageRepository, times(1)).deleteAllByChannelId(channelId);
    verify(readStatusRepository, times(1)).deleteAllByChannelId(channelId);
    verify(channelRepository, never()).deleteById(channelId);
  }

  /**
   * 조회 - findByUserId
   */
  @DisplayName("유저 Id로 채널 초회 테스트")
  @Test
  void findByUserIdChannelTest() {
    // =============== given ===============
    UUID userId = UUID.randomUUID();
    UUID channelId1 = UUID.randomUUID();
    UUID channelId2 = UUID.randomUUID();
    UUID channelId3 = UUID.randomUUID();

    // Mock User and Channels
    User mockUser = mock(User.class);
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    Channel publicChannel1 = new Channel(ChannelType.PUBLIC, "Public Channel 1",
        "Public Description 1");
    Channel publicChannel2 = new Channel(ChannelType.PUBLIC, "Public Channel 2",
        "Public Description 2");

    // Set IDs using reflection or assuming setters exist
    // https://stackoverflow.com/questions/28758883/best-practice-setting-a-field-without-setters-in-a-unit-test
    ReflectionTestUtils.setField(privateChannel, "id", channelId1);
    ReflectionTestUtils.setField(publicChannel1, "id", channelId2);
    ReflectionTestUtils.setField(publicChannel2, "id", channelId3);

    // Mock ReadStatus objects (only for private)
    List<ReadStatus> readStatuses = List.of(
        new ReadStatus(mockUser, privateChannel, Instant.now()));

    // Mock channels returned from repository
    List<Channel> allChannels = List.of(privateChannel, publicChannel1, publicChannel2);

    // Mock DTOs
    ChannelDto subscribedChannelDto = new ChannelDto(channelId1, ChannelType.PRIVATE,
        null, null, List.of(), Instant.now());
    ChannelDto publicChannelDto1 = new ChannelDto(channelId2, ChannelType.PUBLIC,
        "Public Channel 1", "Public Description 1", List.of(), Instant.now());
    ChannelDto publicChannelDto2 = new ChannelDto(channelId3, ChannelType.PUBLIC,
        "Public Channel 2", "Public Description 2", List.of(), Instant.now());

    // Mock method calls
    when(userRepository.existsById(userId)).thenReturn(true);
    when(readStatusRepository.findAllByUserId(userId)).thenReturn(readStatuses);
    when(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(channelId1)))
        .thenReturn(allChannels);
    when(channelMapper.toDto(privateChannel)).thenReturn(subscribedChannelDto);
    when(channelMapper.toDto(publicChannel1)).thenReturn(publicChannelDto1);
    when(channelMapper.toDto(publicChannel2)).thenReturn(publicChannelDto2);

    // =============== when ===============
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // =============== then ===============
    verify(readStatusRepository).findAllByUserId(userId);
    verify(channelRepository).findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(channelId1));
    verify(channelMapper, times(3)).toDto(any(Channel.class));
    assertEquals(3, result.size());
  }

  @DisplayName("유저 Id로 채널 조회 테스트 실패 - 유저 존재하지 않음")
  @Test
  void findAllByUserId_Failure_UserDoesNotExist() {
    // ============ given ============
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId))
        .thenReturn(false);

    // ============ when ============
    assertThrows(UserNotFoundException.class,
        () -> channelService.findAllByUserId(userId));

    // ============ then ============
    verify(readStatusRepository, never()).findAllByUserId(userId);
    verify(channelRepository, never()).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
    verify(channelMapper, never()).toDto(any(Channel.class));
  }

  @DisplayName("채널 조회 실패 - 데이터 무결성 오류 (ReadStatus에 채널이 없음)")
  @Test
  void findAllByUserId_Failure_DataIntegrityError() {
    // =============== given ===============
    UUID userId = UUID.randomUUID();
    ReadStatus readStatusNullChannel = mock(ReadStatus.class);
    when(readStatusNullChannel.getChannel()).thenReturn(null);
    when(readStatusRepository.findAllByUserId(userId))
        .thenReturn(List.of(readStatusNullChannel));
    when(userRepository.existsById(userId)).thenReturn(true);

    // =============== when ===============
    assertThrows(NullPointerException.class, () -> {
      channelService.findAllByUserId(userId);
    });

    // =============== then ===============
    verify(channelRepository, never()).findAllByTypeOrIdIn(any(), anyList());
    verify(channelMapper, never()).toDto(any(Channel.class));
  }
}
