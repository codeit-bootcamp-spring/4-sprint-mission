package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    // ================== test user1 ==================
    User testUser1 = new User(
        "Bob",
        "bob@gmail.com",
        "pw123",
        null
    );
    // persistAndFlush
    // - queues the entity into the database
    // - & executes the insert statement
    entityManager.persistAndFlush(testUser1);

    // need to also make userStatus for user
    // so that the query findAllByChannelIdWithAuthor will run properly..
    UserStatus userStatus1 = new UserStatus(
        testUser1,
        Instant.now()
    );
    entityManager.persistAndFlush(userStatus1);

    // ==================  test user2 ==================
    User testUser2 = new User(
        "alice",
        "alice@gmail.com",
        "pw123",
        null
    );
    entityManager.persistAndFlush(testUser2);

    UserStatus userStatus2 = new UserStatus(
        testUser2,
        Instant.now()
    );
    entityManager.persistAndFlush(userStatus2);
  }

  @DisplayName("유저네임으로 유저 조회 테스트")
  @Test
  void existsByUsernameOrEmailTest_Username() {
    // =============== given ===============
    String username = "Bob";
    String email = "non-existent-email";

    // =============== when ===============
    boolean exists = userRepository.existsByUsernameOrEmail(username, email);

    // =============== then ===============
    assertThat(exists).isTrue();
  }

  @DisplayName("이매일로 유저 조회 테스트")
  @Test
  void existsByUsernameOrEmailTest_Email() {
    // =============== given ===============
    String username = "NONE";
    String email = "alice@gmail.com";

    // =============== when ===============
    boolean exists = userRepository.existsByUsernameOrEmail(username, email);

    // =============== then ===============
    assertThat(exists).isTrue();
  }

  @DisplayName("존재하지 않는 유저네임/이매일로 유저 조회 테스트")
  @Test
  void existsByUsernameOrEmailTest_NoneBoth() {
    // =============== given ===============
    String username = "NONE";
    String email = "non-existent-email";

    // =============== when ===============
    boolean exists = userRepository.existsByUsernameOrEmail(username, email);

    // =============== then ===============
    assertThat(exists).isFalse();
  }

  @DisplayName("둘다 존재하는 유저네임/이매일로 유저 조회 테스트")
  @Test
  void existsByUsernameOrEmailTest_ExistBoth() {
    // =============== given ===============
    String username = "Alice";
    String email = "bob@gmail.com";

    // =============== when ===============
    boolean exists = userRepository.existsByUsernameOrEmail(username, email);

    // =============== then ===============
    assertThat(exists).isTrue();
  }

  // ==========================================================================
  @DisplayName("프로필/UserStatus로 유저 조회 테스트")
  @Test
  void findAllWithProfileAndStatusTest() {
    // =============== given ===============

    User user1 = new User(
        "charlie",
        "charlie@gmail.com",
        "pw123",
        new BinaryContent(
            "profile",
            10L,
            "image/png"
        )
    );
    entityManager.persistAndFlush(user1);
    UserStatus userStatus = new UserStatus(
        user1,
        Instant.now()
    );
    entityManager.persistAndFlush(userStatus);

    User user2 = new User(
        "test",
        "test@gmail.com",
        "pw123",
        null
    );
    entityManager.persistAndFlush(user2);
    UserStatus userStatus2 = new UserStatus(
        user2,
        Instant.now()
    );
    entityManager.persistAndFlush(userStatus2);

    // =============== when ===============
    List<User> userList = userRepository.findAllWithProfileAndStatus();

    // =============== then ===============
    // also contains with null profile
    assertThat(userList).hasSize(4);
    assertThat(userList)
        .extracting("username")
        .containsExactlyInAnyOrder("Bob", "alice", "charlie", "test");

  }

  @DisplayName("프로필/UserStatus로 유저 조회 테스트")
  @Test
  void findAllWithProfileAndStatusTest_NoUserStatus() {
    // =============== given ===============

    User user1 = new User(
        "charlie",
        "charlie@gmail.com",
        "pw123",
        null
    );
    entityManager.persistAndFlush(user1);

    User user2 = new User(
        "test",
        "test@gmail.com",
        "pw123",
        null
    );
    entityManager.persistAndFlush(user2);
    UserStatus userStatus2 = new UserStatus(
        user2,
        Instant.now()
    );
    entityManager.persistAndFlush(userStatus2);

    // =============== when ===============
    List<User> userList = userRepository.findAllWithProfileAndStatus();

    // =============== then ===============
    // also contains with null profile
    assertThat(userList).hasSize(3);
    assertThat(userList)
        .extracting("username")
        .containsExactlyInAnyOrder("Bob", "alice", "test");

  }


}
