package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserStatusRepository userStatusRepository;
    @Mock UserMapper userMapper;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock BinaryContentStorage binaryContentStorage;

    @InjectMocks BasicUserService service;

    @Test
    void 프로필_첨부와_함께_생성_성공() {
        //given
        UserCreateRequest request = new UserCreateRequest("alice", "alice@nate.com", "alice1234");
        BinaryContentCreateRequest profileReq = new BinaryContentCreateRequest("profile.png", "image/png", new byte[]{1, 2, 3});

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);

        given(binaryContentRepository.save(any(BinaryContent.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(binaryContentStorage.put(any(), any(byte[].class))).willReturn(null);

        given(userRepository.save(any(User.class)))
                .willReturn(new User("alice","alice@nate.com","alice1234",null));

        // 매퍼는 엔티티 기반으로 DTO 생성(간단히)
        given(userMapper.toDto(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(),
                    /*profileId*/ null, true);
        });

        //when
        UserDto dto = service.create(request, Optional.of(profileReq));

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.username()).isEqualTo("alice");
        assertThat(dto.email()).isEqualTo("alice@nate.com");

        then(userRepository).should().existsByEmail("alice@nate.com");
        then(userRepository).should().existsByUsername("alice");
        then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
        then(binaryContentStorage).should(times(1)).put(any(), eq(new byte[]{1, 2, 3}));
        then(userRepository).should(times(1)).save(any(User.class));
        then(userMapper).should(times(1)).toDto(any(User.class));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser.getProfile()).isNotNull();
        assertThat(savedUser.getProfile().getFileName()).isEqualTo("profile.png");
        assertThat(savedUser.getProfile().getContentType()).isEqualTo("image/png");
    }

    @Test
    void 이메일_중복으로_유저_생성_실패() {
        //given
        UserCreateRequest request = new UserCreateRequest("alice", "alice@nate.com", "alice1234");
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        //when + then
        assertThrows(DuplicateEmailException.class, () -> service.create(request, Optional.empty()));
        then(userRepository).should().existsByEmail(anyString());
        then(userRepository).should(never()).existsByUsername(anyString());
        then(userRepository).should(never()).save(any());
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();

    }

    @Test
    void 사용자명_중복으로_유저_생성_실패() {
        //given
        UserCreateRequest request = new UserCreateRequest("alice", "alice@nate.com", "alice1234");
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        //when + then
        assertThrows(DuplicateUsernameException.class, () -> service.create(request, Optional.empty()));
        then(userRepository).should().existsByEmail(anyString());
        then(userRepository).should().existsByUsername(anyString());
        then(userRepository).should(never()).save(any());
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    void 사용자_수정_성공() {
        //given
        UUID userId = UUID.randomUUID();
        User existing = new User("alice", "alice@nate.com", "alice1234", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(existing));
        UserUpdateRequest request = new UserUpdateRequest("bob", "bob@nate.com", "bob1234");
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);

        given(userMapper.toDto(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserDto(
                    UUID.randomUUID(),
                    user.getUsername(),
                    user.getEmail(),
                    null, //프로필 변경X
                    true
            );
        });

        //when
        UserDto update = service.update(userId, request, Optional.empty());

        //then
        assertThat(update).isNotNull();
        assertThat(update.username()).isEqualTo("bob");
        assertThat(update.email()).isEqualTo("bob@nate.com");

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("bob@nate.com");
        then(userRepository).should().existsByUsername("bob");
        then(userMapper).should(times(1)).toDto(any(User.class));

        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();

        // 상태 검증: 매퍼로 전달된 User가 실제로 업데이트 되었고 프로필은 그대로 null
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userMapper).should().toDto(userCaptor.capture());
        User updated = userCaptor.getValue();

        assertThat(updated.getUsername()).isEqualTo("bob");
        assertThat(updated.getEmail()).isEqualTo("bob@nate.com");
        assertThat(updated.getPassword()).isEqualTo("bob1234");
        assertThat(updated.getProfile()).isNull(); // 프로필 변경 없음

    }

    @Test
    void 사용자_수정_실패_사용자_없음() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("bob", "bob@nate.com", "bob1234");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when + then
        assertThrows(UserNotFoundException.class, () -> service.update(userId, request, Optional.empty()));

        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).existsByEmail(anyString());
        then(userRepository).should(never()).existsByUsername(anyString());
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    void 사용자_삭제_성공() {
        //given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(true);

        //when
        service.delete(userId);

        //then
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).existsById(userId);
        inOrder.verify(userRepository).deleteById(userId);
        inOrder.verifyNoMoreInteractions();

        then(userStatusRepository).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
    }

    @Test
    void 사용자_삭제_실패() {
        //given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(false);

        //when + then
        assertThrows(UserNotFoundException.class, () -> service.delete(userId));

        then(userRepository).should().existsById(userId);
        then(userRepository).should(never()).deleteById(any());

        then(userStatusRepository).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
    }
}
