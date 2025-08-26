package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MessageIntegrationTest {

  @Autowired private MessageService messageService;

  @Autowired private ChannelService channelService;

  @Autowired private UserService userService;

  @Autowired private MessageRepository messageRepository;

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_success() {
    // given
    UserDto user =
        userService.create(
            new UserCreateRequest("alice", "alice@example.com", "1234"),
            java.util.Optional.empty());
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("general", "main"));

    MessageCreateRequest request =
        new MessageCreateRequest("Hello world!", channel.id(), user.id());

    // when
    MessageDto createdMessage = messageService.create(request, List.of());

    // then
    assertThat(createdMessage).isNotNull();
    assertThat(createdMessage.content()).isEqualTo("Hello world!");
    assertThat(messageRepository.existsById(createdMessage.id())).isTrue();
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_success() {
    // given
    UserDto user =
        userService.create(
            new UserCreateRequest("bob", "bob@example.com", "1234"), java.util.Optional.empty());
    ChannelDto channel = channelService.create(new PublicChannelCreateRequest("dev", "dev talk"));

    MessageDto message =
        messageService.create(
            new MessageCreateRequest("Old Content", channel.id(), user.id()), List.of());

    // when
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("New Content!");
    MessageDto updatedMessage = messageService.update(message.id(), updateRequest);

    // then
    assertThat(updatedMessage.content()).isEqualTo("New Content!");
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_success() {
    // given
    UserDto user =
        userService.create(
            new UserCreateRequest("tom", "tom@example.com", "1234"), java.util.Optional.empty());
    ChannelDto channel =
        channelService.create(new PublicChannelCreateRequest("random", "random chat"));

    MessageDto message =
        messageService.create(
            new MessageCreateRequest("to be deleted", channel.id(), user.id()), List.of());

    // when
    messageService.delete(message.id());

    // then
    assertThat(messageRepository.existsById(message.id())).isFalse();
  }

  @Test
  @DisplayName("메시지 조회 성공")
  void getMessage_success() {
    // given
    UserDto user =
        userService.create(
            new UserCreateRequest("jane", "jane@example.com", "1234"), java.util.Optional.empty());
    ChannelDto channel =
        channelService.create(new PublicChannelCreateRequest("notice", "notice board"));

    MessageDto message =
        messageService.create(
            new MessageCreateRequest("check this out", channel.id(), user.id()), List.of());

    // when
    MessageDto foundMessage = messageService.find(message.id());

    // then
    assertThat(foundMessage).isNotNull();
    assertThat(foundMessage.id()).isEqualTo(message.id());
    assertThat(foundMessage.content()).isEqualTo("check this out");
  }
}
