package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

public class UserRepositoryTest extends RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private BinaryContent profile;

    @Test
    @DisplayName("중복된 이메일의 사용자는 저장할 수 없다")
    void saveUserFailIfDuplicateEmail() {
        //given
        User user = User.builder()
            .username("first")
            .email("first@first.com")
            .password("first")
            .build();

        User user2 = User.builder()
                .username("second")
                .email("first@first.com")
                .password("second")
                .build();

        setRelationEntity(user);
        setRelationEntity(user2);

        userRepository.saveAndFlush(user);

        //when & then

        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("중복된 이름의 사용자는 저장할 수 없다")
    void saveUserFailIfDuplicateName() {
        //given
        final User user = User.builder()
                .username("first")
                .email("first@first.com")
                .password("first")
                .build();

        final User user2 = User.builder()
                .username("first")
                .email("second@second.com")
                .password("second")
                .build();

        setRelationEntity(user);
        setRelationEntity(user2);

        userRepository.saveAndFlush(user);

        //when & then
        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이메일로 이미 가입한 사용자를 조회할 수 있다")
    void findUserByEmailIfUserExists() {
        //given
        User user = User.builder()
                .username("first")
                .email("first@first.com")
                .password("first")
                .build();

        userRepository.save(user);

        //when
        Optional<User> findUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(findUser).isPresent();
        assertThat(user).isEqualTo(findUser.get());
    }

    @Test
    @DisplayName("사용자 이름으로 사용자를 조회할 수 있다")
    void findUserByUsernameIfUserExists() {
        //given
        User user = User.builder()
                .username("first")
                .email("first@first.com")
                .password("first")
                .build();

        userRepository.save(user);

        //when
        Optional<User> findUser = userRepository.findByUsername(user.getUsername());

        //then
        assertThat(findUser).isPresent();
        assertThat(user).isEqualTo(findUser.get());
    }

    @Test
    @DisplayName("가입되지 않은 이름으로 사용자를 조회 시 빈 값을 반환한다")
    void findUserByUsernameReturnOptionalEmptyIfUserNotExists() {
        //when
        Optional<User> findUser = userRepository.findByUsername("notExist");

        //then
        assertThat(findUser).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 이메일로 조회할 시 빈 값을 반환한다")
    void findUserByEmailReturnOptionalEmptyIfUserNotExists() {
        //when
        Optional<User> findUser = userRepository.findByEmail("notExist@email.com");

        //then
        assertThat(findUser).isEmpty();

    }

    private void setRelationEntity(User user) {
        profile = new BinaryContent("test", "test", 1024L, "test".getBytes());
        user.updateProfile(profile);
    }

}
