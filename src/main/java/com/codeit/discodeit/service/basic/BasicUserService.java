package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.user_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.exception.DuplicateUserException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.repository.BinaryContentRepository;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor // NOTE 생성자 lombok에서 생성 final이 붙은 필드를 매개변수로 받아 생성자에서 주입
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusService userStatusService;

  @Override
  public User createUser(UserCreateRequest userCreateRequest) throws IOException {
    final String DEFAULT_PROFILE_IMG_PATH = "./src/main/resources/profileImg/";

    byte[] profileImageBytes;
    String profileImagePath = DEFAULT_PROFILE_IMG_PATH + "basicUserProfileImage.png";
    try (FileInputStream fis = new FileInputStream(profileImagePath)) {
      profileImageBytes = fis.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage(), e);
    }

    BinaryContent profileImg = new BinaryContent(profileImagePath,
        BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);

    MultipartFile profileImage = userCreateRequest.getProfileImage();
    if (profileImage != null) {
      String fileName = profileImage.getOriginalFilename(); // 업로드된 파일의 원본 이름을 가져옵니다.
      Path savePath = Paths.get(DEFAULT_PROFILE_IMG_PATH, fileName); // 파일을 저장할 경로를 설정합니다.
      Files.createDirectories(savePath.getParent()); // 상위 디렉터리가 없으면 생성합니다.
      profileImage.transferTo(savePath); // 업로드된 파일을 지정된 경로로 저장합니다.

      profileImagePath =
          DEFAULT_PROFILE_IMG_PATH + userCreateRequest.getProfileImage().getOriginalFilename();

      try (FileInputStream fis = new FileInputStream(profileImagePath)) {
        profileImageBytes = fis.readAllBytes();
      } catch (IOException e) {
        throw new RuntimeException("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage(), e);
      }

      profileImg = new BinaryContent(profileImagePath,
          BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);
    }
    String userName = userCreateRequest.getUsername();
    String userEmail = userCreateRequest.getEmail();

    String rawPassword = userCreateRequest.getPassword();

    validateUserNameNotDuplicated(userName);
    validateUserEmailNotDuplicated(userEmail);

    User user = new User(userName, rawPassword, userEmail, profileImg.getId());

    profileImg.setReferenceId(user.getId());
    binaryContentRepository.createBinaryContent(profileImg);

    List<Channel> channels = channelRepository.loadChannels();
    for (Channel channel : channels) {
      channel.addUser(user);
    }
    channelRepository.saveChannels(channels);
    userRepository.createUser(user);

    return user;
  }


  @Override
  public User updateUser(UserUpdateRequest userUpdateRequest, MultipartFile profileImage)
      throws IOException {
    User targetUser = findActiveUserByUserId(userUpdateRequest.getUserId());

    // 사용자 기본 정보 수정
    if (userUpdateRequest.getNewEmail() != null && !userUpdateRequest.getNewEmail()
        .equals(targetUser.getEmail())) {
      targetUser.setEmail(userUpdateRequest.getNewEmail());
    }

    if (userUpdateRequest.getNewUsername() != null && !userUpdateRequest.getNewUsername()
        .equals(targetUser.getUsername())) {
      targetUser.setUsername(userUpdateRequest.getNewUsername());
    }

    if (userUpdateRequest.getNewPassword() != null && !userUpdateRequest.getNewPassword()
        .equals(targetUser.getPassword())) {
      targetUser.setPassword(userUpdateRequest.getNewPassword());
    }

    // 프로필 이미지 업데이트
    if (profileImage != null && !profileImage.isEmpty()) {
      // 이미지 바이트 가져오기
      byte[] profileImageBytes = profileImage.getBytes();
      String profileImagePath = profileImage.getOriginalFilename();

      // 실제 비교 로직이 필요한 경우 내부적으로 구현된 compareProfile 메서드 활용
      if (!compareProfile(targetUser, profileImagePath)) {
        // 기존 프로필 삭제
        if (targetUser.getProfileId() != null) {
          binaryContentRepository.deleteBinaryContentByBinaryContentId(targetUser.getProfileId());
        }

        // 새 프로필 이미지 객체 생성 및 저장
        BinaryContent profileImg = new BinaryContent(
            profileImagePath,
            BinaryContentType.USER_PROFILE_IMAGE,
            profileImageBytes
        );
        profileImg.setReferenceId(targetUser.getId());

        binaryContentRepository.createBinaryContent(profileImg);
        targetUser.setProfileId(profileImg.getId());
      }
    }

    // DB에 사용자 정보 반영
    userRepository.updateUser(targetUser);
    return targetUser;
  }


  @Override
  public void deleteUser(UUID userId) {

    User user = findActiveUserByUserId(userId);
        /*
        [ ] 관련된 도메인도 같이 삭제합니다.
        BinaryContent(프로필), UserStatus
        */
    binaryContentRepository.deleteBinaryContentByBinaryContentId(user.getProfileId());
    channelRepository.deleteUserFromChannels(user);
    userRepository.deleteUser(user);
  }

  @Override
  public List<UserDto> findAllUserDto() {
    List<User> users = userRepository.loadUsers();
    List<UserDto> userList = new ArrayList<>();

    for (User user : users) {
      UserDto userDto = getUserDtoByUserId(user.getId());
      userList.add(userDto);
    }

    return userList;
  }

  @Override
  public User findUserByUserId(UUID userId) {
    return findActiveUserByUserId(userId);
  }

  private void validateUserNameNotDuplicated(String userName) {
    if (userRepository.findUserByUserName(userName).isPresent()) {
      throw new DuplicateUserException("같은 email 또는 username를 사용하는 User가 이미 존재함",
          (userName + "은 이미 있는 이름입니다."));
    }
  }

  private void validateUserEmailNotDuplicated(String userEmail) {
    if (userRepository.findUserByUserName(userEmail).isPresent()) {
      throw new DuplicateUserException("같은 email 또는 username를 사용하는 User가 이미 존재함",
          (userEmail + "은 이미 있는 이름입니다."));
    }
  }


  private User findActiveUserByUserId(UUID userId) {
    return userRepository.findActiveUserByUserId(userId)
        .orElseThrow(() -> new NoFindUserException("User를 찾을 수 없음",
            ("User with id {" + userId + "} not found")));
  }

  private boolean compareProfile(User user, String newFileName) {
    // 기존 BinaryContent를 통해 파일명 비교
    Optional<BinaryContent> existingContent = binaryContentRepository.findBinaryContentByBinaryContentId(
        user.getProfileId());
    if (existingContent.isEmpty()) {
      throw new IllegalArgumentException("profileImg <UNK> <UNK>.");
    }
    BinaryContent profileImg = existingContent.get();
    return profileImg.getBinaryContentPath().equals(newFileName);
  }

  private UserDto getUserDtoByUserId(UUID userId) {
    User user = findActiveUserByUserId(userId);
    UserStatus userStatus = userStatusService.findUserStatusByUserId(user.getId());
    Duration duration = Duration.between(userStatus.getLastActiveAt(), Instant.now());
    Boolean loginStatus = duration.toMinutes() < 5;
    return new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
        user.getUsername(), user.getEmail(), user.getProfileId(), loginStatus);
  }
}
