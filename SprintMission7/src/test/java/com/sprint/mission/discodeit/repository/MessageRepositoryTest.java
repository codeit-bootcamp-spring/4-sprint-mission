package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing
public class MessageRepositoryTest {

  @Autowired private MessageRepository messageRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ChannelRepository channelRepository;

  @Test
  @DisplayName("findAllByChannelIdWithAuthor - 성공 케이스")
  void testFindAllByChannelIdWithAuthor_Success() {
    // given
    User testUser = new User("testUser", "test@example.com", "password123", null);
    new UserStatus(testUser, Instant.now()); // UserStatus와 연결
    userRepository.save(testUser);

    Channel testChannel =
        new Channel(ChannelType.PUBLIC, "testChannel", "Test channel description");
    channelRepository.save(testChannel);

    Message message = new Message("Hello", testChannel, testUser, new ArrayList<>());
    messageRepository.save(message);

    // when
    Slice<Message> messages =
        messageRepository.findAllByChannelIdWithAuthor(
            testChannel.getId(), Instant.now().plusSeconds(10), PageRequest.of(0, 10));

    // then
    // 메시지가 정상적으로 조회되는지 확인
    assertThat(messages).isNotEmpty();
    assertThat(messages.getContent()).allMatch(m -> m.getAuthor() != null);
  }

  @Test
  @DisplayName("findAllByChannelIdWithAuthor - 실패 케이스 (없는 채널)")
  void testFindAllByChannelIdWithAuthor_Failure() {
    // given
    // 아무 데이터도 만들지 않고 임의의 채널 ID 사용

    // when
    Slice<Message> messages =
        messageRepository.findAllByChannelIdWithAuthor(
            UUID.randomUUID(), Instant.now().plusSeconds(10), PageRequest.of(0, 10));

    // then
    // 결과는 비어있어야 함
    assertThat(messages.getContent()).isEmpty();
  }

  @Test
  @DisplayName("findLastMessageAtByChannelId - 성공 케이스")
  void testFindLastMessageAtByChannelId_Success() {
    // given
    User testUser = new User("user1", "user1@example.com", "pass123", null);
    new UserStatus(testUser, Instant.now());
    userRepository.save(testUser);

    Channel testChannel = new Channel(ChannelType.PUBLIC, "channel1", "Desc");
    channelRepository.save(testChannel);

    Message message = new Message("Test message", testChannel, testUser, new ArrayList<>());
    messageRepository.save(message);

    // when
    Optional<Instant> lastMessageAt =
        messageRepository.findLastMessageAtByChannelId(testChannel.getId());

    // then
    assertThat(lastMessageAt).isPresent();
  }

  @Test
  @DisplayName("findLastMessageAtByChannelId - 실패 케이스 (없는 채널)")
  void testFindLastMessageAtByChannelId_Failure() {
    // given
    // 데이터 없이 임의의 채널 ID 사용

    // when
    Optional<Instant> lastMessageAt =
        messageRepository.findLastMessageAtByChannelId(UUID.randomUUID());

    // then
    assertThat(lastMessageAt).isNotPresent();
  }
}
