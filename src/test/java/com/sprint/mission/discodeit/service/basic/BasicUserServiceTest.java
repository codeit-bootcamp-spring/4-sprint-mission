package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MockTest;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

class BasicUserServiceTest extends MockTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentMapper binaryContentMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Spy
    @InjectMocks
    private BasicUserService basicUserService;

    private User userWithoutProfile;
    private User userWithProfile;
    private UUID defaultProfileUserId;
    private UUID profileUserId;
    private String username;
    private String email;
    private String password;
    private UserCreateServiceRequest createRequestWithoutProfile;
    private UserCreateServiceRequest createRequestWithProfile;
    private UserResponse userResponse;
    private UserResponse userResponseWithProfile;
    private MockMultipartFile profile;
    private UUID profileId;

    private BinaryContent binaryProfile;


    @BeforeEach
    void setUp() {

        // user without profile
        username = "test";
        email = "test@test.com";
        password = "password";
        defaultProfileUserId = UUID.randomUUID();

        userWithoutProfile = new User(username, email, password, null);
        ReflectionTestUtils.setField(userWithoutProfile, "id", defaultProfileUserId);

        createRequestWithoutProfile = UserCreateServiceRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        userResponse = new UserResponse(userWithoutProfile);

        // user with profile

        profileId = UUID.randomUUID();
        profile = new MockMultipartFile(
                "file",                    // 파라미터 이름
                "test-image.jpg",          // 원본 파일명
                "image/jpeg",              // 컨텐츠 타입
                "content".getBytes()           // 파일 내용
        );

        binaryProfile = BinaryContent.builder()
                .contentType("image/jpeg")
                .bytes("content".getBytes())
                .build();
        ReflectionTestUtils.setField(binaryProfile, "id", profileId);

        userWithProfile = new User(username, email, password, null);

        createRequestWithProfile = UserCreateServiceRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .profile(profile)
                .build();

        userResponse = new UserResponse(userWithProfile);

    }

    @Test
    @DisplayName("프로필 이미지 없는 User 생성 성공")
    void createUserWithoutProfileSuccess() {
        //given
        given(userRepository.save(any(User.class))).willReturn(userWithoutProfile);
        given(userMapper.toResponse(any(User.class))).willReturn(userResponse);
        given(userMapper.toEntity(any(UserCreateServiceRequest.class))).willReturn(userWithoutProfile);

        //when
        UserResponse result = basicUserService.createUser(createRequestWithoutProfile);

        //then
        assertThat(result).isEqualTo(userResponse);
        then(userRepository).should(times(1)).save(any(User.class)); //verify(userRepository, times(1)).save(any(User.class)); 와 동일
    }

    @Test
    @DisplayName("프로필 이미지 있는 유저 생성 성공")
    void createUserSuccessWithProfile() {
        //given
        given(userRepository.save(any(User.class))).willReturn(userWithProfile);
        given(userMapper.toResponse(any(User.class))).willReturn(userResponse);
        given(userMapper.toEntity(any(UserCreateServiceRequest.class))).willReturn(userWithoutProfile);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryProfile);

        //when
        UserResponse result = basicUserService.createUser(createRequestWithProfile);

        //then
        assertThat(result).isEqualTo(userResponse);
        then(userRepository).should(times(1)).save(any(User.class)); //verify(userRepository, times(1)).save(any(User.class)); 와 동일
        then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
//        then(binaryContentStorage).should(times(1)).put(any(UUID.class), any(byte[].class));  테스트 불가능한 부분

    }

    @Test
    @DisplayName("중복 이메일 User 생성 시도 시 실패")
    void createUserFailWhenEmailDuplicated() {
        //given
        given(userRepository.findByEmail(eq(email))).willThrow(UserException.class);

        //when & then
        assertThatThrownBy(() -> basicUserService.createUser(createRequestWithoutProfile))
                .isInstanceOf(UserException.class);

    }

    @Test
    @DisplayName("중복 이름 User 생성 시도 시 실패")
    void createUserFailWhenUsernameDuplicated() {
        //given
        given(userRepository.findByUsername(eq(username))).willThrow(UserException.class);

        //when & then
        assertThatThrownBy(() -> basicUserService.createUser(createRequestWithoutProfile))
                .isInstanceOf(UserException.class);
    }


    @Test
    @DisplayName("User 업데이트 성공")
    void updateUserSuccess() {
        //given
        String updateUsername = "update";
        String updateEmail = "update@update.com";
        String updatePassword = "updatePassword";

        UserUpdateServiceRequest updateRequest = UserUpdateServiceRequest.builder()
                .userId(defaultProfileUserId)
                .newUsername(updateUsername)
                .newEmail(updateEmail)
                .newPassword(updatePassword)
                .build();

        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(userWithoutProfile));
        given(userMapper.toResponse(any(User.class))).willReturn(userResponse);

        //when
        UserResponse result = basicUserService.updateUser(updateRequest);

        //then
        assertThat(result).isEqualTo(userResponse);


    }

    @Test
    @DisplayName("존재하지 않는 User 업데이트 시도 시 실패")
    void updateUserFailWhenUserNotExist() {
        //given
        UserUpdateServiceRequest updateRequest = UserUpdateServiceRequest.builder()
                .userId(defaultProfileUserId)
                .build();

        //when & then
        assertThatThrownBy(() -> basicUserService.updateUser(updateRequest))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("User 삭제 성공")
    void deleteUserSuccess() {
        //when
        basicUserService.deleteUser(defaultProfileUserId);

        //then
        then(userRepository).should(times(1)).deleteById(any(UUID.class));
    }
}
