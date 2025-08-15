package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelUpdatePrivateChannelException;
import com.sprint.mission.discodeit.exception.channel.ChannelWithNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ChannelIntegrationTest {

  @Autowired
  ChannelService channelService;
  @Autowired
  ChannelRepository channelRepository;
  @Autowired
  UserRepository userRepository;

  /**
   * POST - create (PUBLIC)
   */
  @DisplayName("공개 채널 생성과 저장 테스트")
  @Test
  void CreatePublicChannelAndSavesChannel() {
    // ================== given ==================
    String name = "new Channel";
    String description = "This is new channel";
    PublicChannelCreateRequest channelCreateRequestDto = new PublicChannelCreateRequest(
        name,
        description
    );

    // ================== when ==================
    ChannelDto channelDto = channelService.create(channelCreateRequestDto);

    // ================== then ==================
    Channel savedChannel = channelRepository.findById(channelDto.id()).orElseThrow();
    assertThat(savedChannel.getName()).isEqualTo(name);
    assertThat(savedChannel.getDescription()).isEqualTo(description);
    assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @DisplayName("채널 생성과 저장 실패 - 채널 이름 중복")
  @Test
  void createPublicChannel_withDuplicateName_Failure() {
    // creating & saving channel
    String existingName = "channel name";
    channelRepository.save(new Channel(
        ChannelType.PUBLIC,
        existingName,
        "This is new channel"
    ));

    PublicChannelCreateRequest channelCreateRequestDto = new PublicChannelCreateRequest(
        existingName, // same name
        "description"
    );

    assertThatThrownBy(() -> channelService.create(channelCreateRequestDto))
        .isInstanceOf(
            ChannelWithNameAlreadyExistsException.class); // Or a more specific custom exception
  }


  /**
   * POST - create (PRIVATE)
   */
  @DisplayName("비공개 채널 생성과 저장 테스트")
  @Test
  void CreatePrivateChannelAndSavesChannel() {
    // ================== given ==================
    User user1 = new User(
        "user1",
        "user1@gmail.com",
        "pw1234",
        null
    );
    User user2 = new User(
        "user2",
        "user2@gmail.com",
        "pw1234",
        null
    );
    userRepository.saveAll(List.of(user1, user2));

    List<UUID> participantIds = List.of(user1.getId(), user2.getId());
    PrivateChannelCreateRequest channelCreateRequestDto = new PrivateChannelCreateRequest(
        participantIds);

    // ================== when ==================
    ChannelDto channelDto = channelService.create(channelCreateRequestDto);

    // ================== then ==================
    Channel savedChannel = channelRepository.findById(channelDto.id()).orElseThrow();
    assertThat(savedChannel.getType()).isEqualTo(ChannelType.PRIVATE);
    assertThat(savedChannel.getName()).isNull();
  }

  @DisplayName("비공개 채널 생성과 저장 실패 - 유저 존재하지 않음")
  @Test
  void createPrivateChannel_withNonExistentUser_Failure() {
    // ================== given ==================
    User validUser = new User(
        "validUser",
        "validUser@gmail.com",
        "pw1234",
        null
    );
    userRepository.save(validUser);

    UUID nonExistentUserId = UUID.randomUUID();

    List<UUID> participantIds = List.of(validUser.getId(), nonExistentUserId);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

    // ================== when & then ==================
    assertThatThrownBy(() -> channelService.create(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  /**
   * PATCH - update (PUBLIC only)
   */
  @DisplayName("(공개) 채널 수정과 저장 테스트")
  @Test
  void updateChannelAndSavesChannel() {
    // ================== given ==================
    // create & save channel that we will edit
    String name = "new Channel";
    String description = "This is new channel";
    PublicChannelCreateRequest channelCreateRequestDto = new PublicChannelCreateRequest(
        name,
        description
    );
    // saves
    ChannelDto channelDto = channelService.create(channelCreateRequestDto);

    String newName = "updated name";
    String newDescription = "updated description";
    PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );

    // ================== when ==================

    channelService.update(channelDto.id(), channelUpdateRequest);

    // ================== then ==================
    Channel updatedChannel = channelRepository.findById(channelDto.id()).orElseThrow();
    assertThat(updatedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(updatedChannel.getName()).isEqualTo(newName);
    assertThat(updatedChannel.getDescription()).isEqualTo(newDescription);
  }

  @DisplayName("(공개) 채널 수정과 저장 실패 - 채널 존재하지 않음")
  @Test
  void updateChannelAndSavesChannel_ChannelDoesNotExist_Failure() {
    // ================== given ==================
    String newName = "updated name";
    String newDescription = "updated description";
    PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );
    UUID nonExistentChannelId = UUID.randomUUID();

    // ================== when ==================

    assertThatThrownBy(() -> channelService.update(nonExistentChannelId, channelUpdateRequest))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @DisplayName("(공개) 채널 수정과 저장 실패 - 비공개 채널")
  @Test
  void updateChannelAndSavesChannel_ChannelIsPrivate_Failure() {
    // ================== given ==================
    User user1 = new User(
        "user1",
        "user1@gmail.com",
        "pw1234",
        null
    );
    User user2 = new User(
        "user2",
        "user2@gmail.com",
        "pw1234",
        null
    );
    userRepository.saveAll(List.of(user1, user2));

    List<UUID> participantIds = List.of(user1.getId(), user2.getId());
    PrivateChannelCreateRequest channelCreateRequestDto = new PrivateChannelCreateRequest(
        participantIds);
    // saved private channel
    ChannelDto savedPrivateChannel = channelService.create(channelCreateRequestDto);

    // update request
    String newName = "updated name";
    String newDescription = "updated description";
    PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );

    // ================== when & then ==================
    assertThatThrownBy(() -> channelService.update(savedPrivateChannel.id(), channelUpdateRequest))
        .isInstanceOf(ChannelUpdatePrivateChannelException.class);
  }

  /**
   * DELETE
   */
  @DisplayName("채널 삭제 테스트")
  @Test
  void deleteChannel() {
    // ================== given ==================
    // create & save channel that we will edit
    String name = "new Channel";
    String description = "This is new channel";
    PublicChannelCreateRequest channelCreateRequestDto = new PublicChannelCreateRequest(
        name,
        description
    );
    // saves
    ChannelDto channelDto = channelService.create(channelCreateRequestDto);

    // ================== when ==================

    channelService.delete(channelDto.id());

    // ================== then ==================
    Optional<Channel> deletedChannel = channelRepository.findById(channelDto.id());
    assertThat(deletedChannel).isEmpty();
  }

  @DisplayName("채널 삭제 실패 - 채널 존재하지 않음")
  @Test
  void deleteChannelChannelDoesNotExist_Failure() {

    // ================== given ==================
    UUID nonExistentChannelId = UUID.randomUUID();

    // ================== when & then ==================
    assertThatThrownBy(() -> channelService.delete(nonExistentChannelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }
}
