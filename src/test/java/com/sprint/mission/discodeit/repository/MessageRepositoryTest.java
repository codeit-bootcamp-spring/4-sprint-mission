package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
public class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  // pre-populating data
  @Autowired
  private TestEntityManager entityManager;

  private Channel testChannel;
  private Instant afterSetup;

  @BeforeEach
  void setUp() {
    // test user
    User testUser = new User(
        "Bob",
        "bob@gmail.com",
        "pw123",
        null
    );
    // persistAndFlush
    // - queues the entity into the database
    // - & executes the insert statement
    entityManager.persistAndFlush(testUser);

    // need to also make userStatus for user so that the query findAllByChannelIdWithAuthor will run properly..
    UserStatus userStatus = new UserStatus(
        testUser,
        Instant.now()
    );
    entityManager.persistAndFlush(userStatus);

    // test channel
    testChannel = new Channel(
        ChannelType.PUBLIC,
        "public channel",
        "hello"
    );
    entityManager.persistAndFlush(testChannel);

    Message message1 = new Message(
        "content1",
        testChannel,
        testUser,
        null
    );
    // saveAndFlush -> writes to the DB and set the audit date immediately
    messageRepository.saveAndFlush(message1);

    Message message2 = new Message(
        "content2",
        testChannel,
        testUser,
        null
    );
    messageRepository.saveAndFlush(message2);

    afterSetup = Instant.now();
  }

  @DisplayName("메세지 조회 테스트")
  @Test
  void findAllByChannelIdWithAuthorTest() {

    // =============== given ===============
    Pageable pageable = PageRequest.of(0, 10);
    Instant createdAt = afterSetup.plusSeconds(60);
    // =============== when ===============

    Slice<Message> sliceMessages = messageRepository.findAllByChannelIdWithAuthor(
        testChannel.getId(),
        createdAt,
        pageable
    );
    List<Message> messageList = sliceMessages.getContent();

    // =============== then ===============
    // assert the result from the actual database (not using verify)
    assertThat(messageList).hasSize(2);
    assertThat(messageList)
        .extracting("content")
        .containsExactlyInAnyOrder("content1", "content2");
  }

  @DisplayName("메세지 조회 테스트 실패 - 채널 존재하지 않음")
  @Test
  void findLastMessageAtByChannelIdTest_failure_ChannelDoesNotExist() {
    // =============== given ===============
    Pageable pageable = PageRequest.of(0, 10);
    Instant createdAt = afterSetup.plusSeconds(60);
    UUID nonExistChannelId = UUID.randomUUID();
    // =============== when ===============

    Slice<Message> sliceMessages = messageRepository.findAllByChannelIdWithAuthor(
        nonExistChannelId,
        createdAt,
        pageable
    );
    List<Message> messageList = sliceMessages.getContent();

    // =============== then ===============
    // assert the result from the actual database (not using verify)
    assertThat(messageList).hasSize(0);
  }

  @DisplayName("가장 최근 메세지 조회 테스트")
  @Test
  void findLastMessageAtByChannelIdTest() {
    // =============== given ===============
    UUID channelId = testChannel.getId();
    // =============== when ===============

    Optional<Instant> lastMessageAtByChannelId = messageRepository.findLastMessageAtByChannelId(
        channelId
    );

    // =============== then ===============
    // assert the result from the actual database (not using verify)
    assertThat(lastMessageAtByChannelId).isPresent();

  }

  @DisplayName("메세지 조회 테스트 실패 - 채널 존재하지 않음")
  @Test
  void findLastMessageAtByChannelId_Failure_ChannelDoesNotExist() {
    // =============== given ===============
    UUID nonExistChannelId = UUID.randomUUID();
    // =============== when ===============

    Optional<Instant> lastMessageAtByChannelId = messageRepository.findLastMessageAtByChannelId(
        nonExistChannelId
    );

    // =============== then ===============
    // assert the result from the actual database (not using verify)
    assertThat(lastMessageAtByChannelId).isEmpty();

  }
}
