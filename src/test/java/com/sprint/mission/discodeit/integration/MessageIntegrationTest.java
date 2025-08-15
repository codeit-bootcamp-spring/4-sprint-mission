package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MessageIntegrationTest {

  @Autowired
  UserService userService;
  @Autowired
  ChannelRepository channelRepository;
  @Autowired
  MessageService messageService;
  @Autowired
  MessageRepository messageRepository;

  private UserDto userDto;
  private Channel savedChannel;

  @BeforeEach
  void setUp() {
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        "Bob",
        "bob@gmail.com",
        "pw123"
    );

    // using service instead of repository because it handles saving userstatus too
    userDto = userService.create(userCreateRequestDto, Optional.empty());

    // creating & saving channel
    Channel channel = new Channel(
        ChannelType.PUBLIC,
        "test name",
        "test description"
    );
    savedChannel = channelRepository.save(channel);
  }

  /**
   * POST - create
   */
  @DisplayName("메세지 생성과 저장 테스트")
  @Test
  void CreateMessageAndSavesMessage() {
    // ================== given ==================

    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Hello!",
        savedChannel.getId(),
        userDto.id()
    );
    // ================== when ==================
    MessageDto messageDto = messageService.create(messageCreateRequest, null);

    // ================== then ==================
    Message savedMessage = messageRepository.findById(messageDto.id()).orElseThrow();
    assertThat(savedMessage.getContent()).isEqualTo("Hello!");
    assertThat(savedMessage.getChannel().getId()).isEqualTo(savedChannel.getId());
    assertThat(savedMessage.getAuthor().getId()).isEqualTo(userDto.id());
  }

  @DisplayName("메세지 생성과 저장 실패 - 채널 존재하지 않음")
  @Test
  void CreateMessageAndSavesMessage_ChannelDoesNotExist_Failure() {
    // ================== given ==================
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Hello!",
        UUID.randomUUID(),
        userDto.id()
    );

    // ================== when & then ==================
    assertThatThrownBy(() -> messageService.create(messageCreateRequest, null))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @DisplayName("메세지 생성과 저장 실패 - 유저 존재하지 않음")
  @Test
  void CreateMessageAndSavesMessage_UserDoesNotExist_Failure() {
    // ================== given ==================

    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Hello!",
        savedChannel.getId(),
        UUID.randomUUID()
    );

    // ================== when & then ==================
    assertThatThrownBy(() -> messageService.create(messageCreateRequest, null))
        .isInstanceOf(UserNotFoundException.class);
  }

  /**
   * PATCH - update
   */
  @DisplayName("메세지 수정과 저장 테스트")
  @Test
  void updateMessageAndSavesMessage() {
    // ================== given ==================
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Hello!",
        savedChannel.getId(),
        userDto.id()
    );
    MessageDto messageDto = messageService.create(messageCreateRequest, null);

    // updated message
    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "New Content"
    );
    // ================== when ==================
    messageService.update(messageDto.id(), messageUpdateRequest);
    Message savedMessage = messageRepository.findById(messageDto.id()).orElseThrow();
    assertThat(savedMessage.getContent()).isEqualTo("New Content");
    assertThat(savedMessage.getChannel().getId()).isEqualTo(savedChannel.getId());
    assertThat(savedMessage.getAuthor().getId()).isEqualTo(userDto.id());

  }

  @DisplayName("메세지 수정과 저장 실패 - 메세지 존재하지 않음")
  @Test
  void updateMessageAndSavesMessage_MessageDoesNotExist_Failure() {
    // ================== given ==================
    UUID nonExistentMessageId = UUID.randomUUID();
    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "New Content"
    );
    // ================== when ==================
    assertThatThrownBy(() -> messageService.update(nonExistentMessageId, messageUpdateRequest))
        .isInstanceOf(MessageNotFoundException.class);
  }

  /**
   * DELETE
   */
  @DisplayName("메세지 삭제 테스트")
  @Test
  void deleteMessage() {
    // ================== given ==================
    // create & save channel that we will edit

    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Hello!",
        savedChannel.getId(),
        userDto.id()
    );
    MessageDto messageDto = messageService.create(messageCreateRequest, null);
    // ================== when ==================
    messageService.delete(messageDto.id());

    // ================== then ==================
    Optional<Message> deletedMessage = messageRepository.findById(messageDto.id());
    AssertionsForClassTypes.assertThat(deletedMessage).isEmpty();

  }

  @DisplayName("메세지 삭제 실패 - 메세지 존재하지 않음")
  @Test
  void deleteMessage_MessageDoesNotExist_Failure() {
    // ================== given ==================
    UUID nonExistentMessageId = UUID.randomUUID();
    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "New Content"
    );
    // ================== when ==================
    assertThatThrownBy(() -> messageService.update(nonExistentMessageId, messageUpdateRequest))
        .isInstanceOf(MessageNotFoundException.class);
  }

  /**
   * GET - findAllByChannelId
   */
  @DisplayName("채널 Id로 모든 메세지 조회 테스트")
  @Test
  void findAllMessages() {
    // ================== given ==================
    // Create messages with some delay to ensure different timestamps
    MessageCreateRequest messageCreateRequest1 = new MessageCreateRequest(
        "Hello!",
        savedChannel.getId(),
        userDto.id()
    );
    messageService.create(messageCreateRequest1, null);

    MessageCreateRequest messageCreateRequest2 = new MessageCreateRequest(
        "Hi!",
        savedChannel.getId(),
        userDto.id()
    );
    messageService.create(messageCreateRequest2, null);

    Instant cursor = Instant.now().plusSeconds(60); // 1 minute in future
    Pageable pageable = PageRequest.of(0, 10);

    // ================== when ==================
    PageResponse<MessageDto> result = messageService.findAllByChannelId(
        savedChannel.getId(),
        cursor,
        pageable
    );

    // ================== then ==================
    assertThat(result).isNotNull();
    assertThat(result.content()).isNotNull();
    assertThat(result.content()).hasSize(2);
    List<String> messageContents = result.content().stream()
        .map(MessageDto::content)
        .toList();
    assertThat(messageContents).containsExactlyInAnyOrder("Hello!", "Hi!");

    //pagination metadata
    assertThat(result.hasNext()).isFalse(); // false if all messages fit in one page
    assertThat(result.nextCursor()).isNotNull(); // createdAt of the last message

  }

  @DisplayName("채널 Id로 모든 메세지 조회 실패 - 존재하지 않는 채널 ID로 조회")
  @Test
  void findAllMessages_ChannelNotFound_ThrowsException() {
    // ================== given ==================
    UUID nonExistentChannelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    // ================== when & then ==================
    assertThatThrownBy(() ->
        messageService.findAllByChannelId(nonExistentChannelId, cursor, pageable)
    ).isInstanceOf(ChannelNotFoundException.class);
  }
}
