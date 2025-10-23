package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    BasicUserService userService;

//    @BeforeEach
//    void setUp() {
//        userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository, userMapper, binaryContentStorage);
//    }

    @Nested
    @DisplayName("create()")
    class CreateUserTests {

        @Test
        void create_shouldThrow_whenUsernameDuplicated() {
            //given
            UserCreateRequest request = new UserCreateRequest("dupName", "dup@example.com", "dup123", null, null);
            when(userRepository.existsByUsername("dupName")).thenReturn(true);

            //when & then
            UserNameAlreadyExistsException thrown = assertThrows(
                    UserNameAlreadyExistsException.class,
                    () -> userService.create(request)
            );
            assertEquals("이미 존재하는 유저명입니다.", thrown.getMessage());
            verify(userRepository).existsByUsername("dupName");
            verify(userRepository, never()).save(any());
        }

        @Test
        void create_shouldThrow_whenEmailDuplicated() {
            //given
            UserCreateRequest request = new UserCreateRequest("name", "dup@example.com", "dup123", null, null);
            when(userRepository.existsByUsername("name")).thenReturn(false);
            when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

            //when & then
            EmailAlreadyExistsException thrown = assertThrows(
                    EmailAlreadyExistsException.class,
                    () -> userService.create(request)
            );
            assertEquals("이미 가입된 이메일입니다.", thrown.getMessage());
            verify(userRepository).existsByUsername("name");
            verify(userRepository).existsByEmail("dup@example.com");
            verify(userRepository, never()).save(any());
        }

        @Test
        void create_shouldSaveUserAndProfile_whenFileProvided() throws IOException {
            //given
            //파일 mock 객체로 준비
            MultipartFile file = mock(MultipartFile.class);
            when(file.getOriginalFilename()).thenReturn("file.jpeg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getSize()).thenReturn(1024L);
            byte[] bytes = new byte[]{1, 0, 2, 4};
            when(file.getBytes()).thenReturn(bytes);

            UserCreateRequest request = new UserCreateRequest("name", "dup@example.com", "dup123", file, null);

            when(userRepository.existsByUsername("name")).thenReturn(false);
            when(userRepository.existsByEmail("dup@example.com")).thenReturn(false);

            // binaryContentRepository.save(...) 가 null을 돌려주지 않도록 실제 인자를 그대로 반환
            when(binaryContentRepository.save(any(BinaryContent.class)))
                    .thenAnswer(inv -> inv.getArgument(0));


            //mapper가 저장용 엔티티를 만듦
            User mappedUser = new User();
            when(userMapper.userCreateDtoToUser(eq(request), any())).thenReturn(mappedUser);

            User saved = new User();
            saved.setId(UUID.randomUUID());
            saved.setUsername("name");
            when(userRepository.save(mappedUser)).thenReturn(saved);

            UserDto expectedDto = new UserDto(saved.getId(), saved.getUsername(),saved.getEmail(),null, true);
            when(userMapper.userToUserDto(any(User.class))).thenReturn(expectedDto);

            //when
            UserDto result = userService.create(request);

            //then

            ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
            verify(userMapper).userToUserDto(userArg.capture());
            User passed = userArg.getValue();
            assertThat(passed).isNotNull();
            assertThat(passed.getId()).isEqualTo(saved.getId());
            assertThat(passed.getUsername()).isEqualTo(saved.getUsername());

            assertThat(result).isNotNull();

            ArgumentCaptor<UUID> fileIdCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(binaryContentStorage).put(fileIdCaptor.capture(), eq(bytes));
            UUID fileId = fileIdCaptor.getValue();

            // mapper로 전달된 fileId(프로필 이미지 id)가 storage에 put된 id와 동일한지 검증
            ArgumentCaptor<UUID> savedFileIdCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(userMapper).userCreateDtoToUser(eq(request), savedFileIdCaptor.capture());
            assertThat(savedFileIdCaptor.getValue()).isEqualTo(fileId);

            assertEquals(saved.getId(), result.getId());
            assertEquals(saved.getUsername(), result.getUsername());

            verify(userRepository).save(mappedUser);
            verify(userMapper).userToUserDto(any(User.class));
            verify(binaryContentRepository).save(any(BinaryContent.class));
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateUserTests {
        @Test
        void update_shouldThrow_whenUserNotFound() {
            //given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, userId, null, null, null);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            //when & then
            assertThatThrownBy(() -> userService.update(request))
                    .isInstanceOf(UserNotFoundException.class);
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }

        @Test
        void update_shouldThrow_whenFileStoreFails() throws IOException {
            //given

            //유저가 존재하도록 스텁. 앞단에서 UserNotFoundException 발생 방지
            UUID userId = UUID.randomUUID();
            User user = new User();
            user.setId(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            //파일 제공: 메타데이터를 mock하여 파일 주입
            MultipartFile file = mock(MultipartFile.class);

            //예외 유발 - 파일 바이트 정보 읽기 중
            when(file.getBytes()).thenThrow(new IOException("파일의 크기가 허용 용량을 초과하였습니다."));

            var binaryContentDto = mock(BinaryContentCreateDto.class, RETURNS_DEEP_STUBS);

            UserUpdateRequest request = mock(UserUpdateRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getBinaryContent()).thenReturn(binaryContentDto);
            when(binaryContentDto.getFile()).thenReturn(file);

            //when & then
            assertThatThrownBy(() -> userService.update(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("파일 저장 실패");

            //부수 효과 유무 검증
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
            verify(binaryContentStorage, never()).put(any(), any());
            verify(binaryContentRepository, never()).save(any());
        }

        @Test
        //기존 프로필 이미지가 없는 상태에서 프로필 이미지를 등록했을 때 해당 변경사항을 업데이트 하는지 확인함
        void update_shouldReplaceProfileImage_whenFileProvided() throws IOException {
            //given
            UUID userId = UUID.randomUUID();
            User user = new User();
            user.setId(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            //새로운 파일 mock
            byte[] bytes = new byte[] {1, 2, 3};
            MultipartFile file = mock(MultipartFile.class);
            when(file.getOriginalFilename()).thenReturn("file.jpeg");
            when(file.getContentType()).thenReturn("image/jpeg");
            when(file.getSize()).thenReturn(1024L);
            when(file.getBytes()).thenReturn(bytes);

            BinaryContentCreateDto binaryContent = new BinaryContentCreateDto(file);
            UserUpdateRequest request = mock(UserUpdateRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getNewUsername()).thenReturn(null);
            when(request.getNewEmail()).thenReturn(null);
            when(request.getNewPassword()).thenReturn(null);
            when(request.getBinaryContent()).thenReturn(binaryContent);

            //mapper 반환값 준비
            UserDto mapped = mock(UserDto.class);
            when(userMapper.userToUserDto(user)).thenReturn(mapped);

            //when
            UserDto result = userService.update(request);

            //then
            assertThat(result).isSameAs(mapped);
            verify(userMapper).userToUserDto(user);

            //스토리지가 put을 호출하는지 검증
            ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
            verify(binaryContentStorage).put(captor.capture(), eq(bytes));
            UUID fileId = captor.getValue();

            verify(binaryContentRepository, never()).delete(any());
            verify(binaryContentRepository, never()).save(any());

            //엔티티 상태 검증
            assertThat(user.getProfile()).isNotNull();
            assertThat(user.getProfile().getId()).isEqualTo(fileId);
            assertThat(user.getProfile().getFileName()).isEqualTo("file.jpeg");
            assertThat(user.getProfile().getContentType()).isEqualTo("image/jpeg");
            assertThat(user.getProfile().getSize()).isEqualTo(1024L);

            verify(userRepository,never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteUserTests {
        @Test
        void delete_shouldThrow_whenUserNotFound() {
            // given
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.delete(userId))
                    .isInstanceOf(UserNotFoundException.class);

            verify(binaryContentRepository, never()).delete(any());
            verify(userStatusRepository, never()).delete(any());
            verify(userRepository).findById(userId);
        }

        @Test
        void delete_shouldDeleteUserWithProfileImage_whenFileExists() throws IOException {
            // given
            UUID userId = UUID.randomUUID();
            UUID fileId = UUID.randomUUID();
            User user = new User();
            user.setId(userId);
            var profile = new BinaryContent();
            profile.setId(fileId);
            user.setProfile(profile);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // when
            userService.delete(userId);

            // then
            verify(binaryContentRepository).deleteById(fileId);
            verify(userStatusRepository).deleteById(userId);
            verify(userRepository).deleteById(userId);

        }
    }
}
