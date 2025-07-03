package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UserWithStatusResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor // NOTE 생성자 lombok에서 생성 final이 붙은 필드를 매개변수로 받아 생성자에서 주입
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusService userStatusService;

    @Override
    public UserResponseDto createUser(UserCreateRequestDto userCreateRequestDTO) throws IOException {
        final String DEFAULT_PROFILE_IMG_PATH = "./src/main/resources/profileImg/";

        MultipartFile profileImage = userCreateRequestDTO.getProfileImage();
        String fileName = profileImage.getOriginalFilename(); // 업로드된 파일의 원본 이름을 가져옵니다.
        Path savePath = Paths.get(DEFAULT_PROFILE_IMG_PATH, fileName); // 파일을 저장할 경로를 설정합니다.
        Files.createDirectories(savePath.getParent()); // 상위 디렉터리가 없으면 생성합니다.
        profileImage.transferTo(savePath); // 업로드된 파일을 지정된 경로로 저장합니다.

        String userName = userCreateRequestDTO.getUserName();
        String password = userCreateRequestDTO.getPassword();
        String userEmail = userCreateRequestDTO.getEmail();

        validateUserNameNotDuplicated(userName);
        validateUserEmailNotDuplicated(userEmail);
        byte[] profileImageBytes;

        String profileImagePath = DEFAULT_PROFILE_IMG_PATH + userCreateRequestDTO.getProfileImage().getOriginalFilename();

        try (FileInputStream fis = new FileInputStream(profileImagePath)) {
            profileImageBytes = fis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage(), e);
        }
        BinaryContent profileImg = new BinaryContent(profileImagePath, BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);

        User user = new User(userName, password, userEmail, profileImg.getId());
        userRepository.createUser(user);

        profileImg.setReferenceId(user.getId());
        binaryContentRepository.createBinaryContent(profileImg);

        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                profileImagePath // 프로필 이미지 바이트 직접 반환
        );
    }


    @Override
    public void updateUser(UserUpdateRequestDto userUpdateRequestDTO) {
        User targetUser = findUserByUserId(userUpdateRequestDTO.getUserId());

        targetUser.setUserName(userUpdateRequestDTO.getNewUserName());
        targetUser.setEmail(userUpdateRequestDTO.getNewEmail());

        // 유저의 프로필아이디를 가진 사진의 패스가 같아야함

        if (!compareProfile(userUpdateRequestDTO)) {
            // 프로필 이미지 읽기
            byte[] profileImageBytes = null;
            if (userUpdateRequestDTO.getNewProfileImagePath() != null && !userUpdateRequestDTO.getNewProfileImagePath().isEmpty()) {
                File imageFile = new File(userUpdateRequestDTO.getNewProfileImagePath());
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    profileImageBytes = fis.readAllBytes();
                } catch (IOException e) {
                    System.out.println("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage());
                    userRepository.updateUser(targetUser);
                    return;
                }
            }
            BinaryContent profileImg = new BinaryContent(userUpdateRequestDTO.getNewProfileImagePath(), BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);
            profileImg.setReferenceId(targetUser.getId());
            binaryContentRepository.deleteBinaryContentByBinaryContentId(targetUser.getProfileId());
            targetUser.setProfileId(profileImg.getId());
            binaryContentRepository.createBinaryContent(profileImg);
        }
        
        userRepository.updateUser(targetUser);
    }

    @Override
    public void deleteUser(UserResponseDto userResponseDto) {

        User user = findUserByUserId(userResponseDto.getUserId());
        /*
        [ ] 관련된 도메인도 같이 삭제합니다.
        BinaryContent(프로필), UserStatus
        */
        binaryContentRepository.deleteBinaryContentByBinaryContentId(user.getProfileId());
        channelRepository.deleteUserFromChannels(user);
        userRepository.deleteUser(user);
    }

    @Override
    public void restoreUser(String userName) {
        isExistUserByUserName(userName);
        userRepository.restoreUser(userName);
    }


    @Override
    public List<UserResponseDto> findAllUserDTO(){
        List<User> usersFromFile = userRepository.loadUsers();
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : usersFromFile) {
            userResponseDtos.add(new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), binaryContentRepository.findBinaryContentByBinaryContentId(user.getProfileId()).get().getBytes()));
        }
        return userResponseDtos;
    }

    @Override
    public List<UserResponseDto> findAllActiveUserDTO() {
        return userRepository.loadUsers().stream()
                .filter(user -> user.getStatus() == UserActivationState.ACTIVE)
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        binaryContentRepository
                                .findBinaryContentByBinaryContentId(user.getProfileId()).get()
                                .getBinaryContentPath()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> findAllDeactiveUserDTO() {
        return userRepository.loadUsers().stream()
                .filter(user -> user.getStatus() == UserActivationState.DEACTIVE)
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        ""
                ))
                .collect(Collectors.toList());
    }


    private void validateUserNameNotDuplicated(String userName) {
        if (userRepository.findUserByUserName(userName).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다: " + userName);
        }
    }

    private void validateUserEmailNotDuplicated(String userEmail) {
        if (userRepository.findUserByUserName(userEmail).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용되고 있는 이메일입니다: " + userEmail);
        }
    }

    private void isExistUserByUserName(String userName) {
        userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userName));
    }

    private void isExistUserByUserEmail(String userEmail) {
        userRepository.findUserByUserName(userEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userEmail));
    }

    private void isExistUserByUserId(UUID userId) {
        userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    private boolean compareProfile(UserUpdateRequestDto userUpdateRequestDTO){
        User user = findUserByUserId(userUpdateRequestDTO.getUserId());
        Optional<BinaryContent> profileImgFromFile= binaryContentRepository.findBinaryContentByBinaryContentId(user.getProfileId());

        if(profileImgFromFile.isEmpty()){
            throw new IllegalArgumentException("profileImg 정보가 없습니다.");
        }
        BinaryContent profileImg = profileImgFromFile.get();

        return profileImg.getBinaryContentPath().equals(userUpdateRequestDTO.getNewProfileImagePath());
    }

    private User findUserByUserId(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }

    @Override
    public UserResponseDto findUserDtoByUserName(String userName) {
        return userRepository.findUserByUserName(userName)
                .filter(user -> user.getStatus() == UserActivationState.ACTIVE)
                .map(user -> {
                    /*
                    byte[] profileBytes = null;

                    if (user.getProfileId() != null) {
                        profileBytes = binaryContentsRepository.findBinaryContentsByBinaryContentsId(user.getProfileId())
                                .map(BinaryContents::getBinaryData)
                                .orElse(null);
                    }

                    return new UserResponseDto(
                            user.getId(),
                            user.getUserName(),
                            user.getEmail(),
                            profileBytes
                    );*/ //바이트로 반환

                    Optional<BinaryContent> binaryContent = binaryContentRepository.findBinaryContentByBinaryContentId(user.getProfileId());
                    String profilePath = binaryContent.get().getBinaryContentPath();

                    return new UserResponseDto(
                            user.getId(),
                            user.getUserName(),
                            user.getEmail(),
                            profilePath
                    );

                })
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다: " + userName));
    }
    @Override
    public UserResponseDto findUserDtoByUserId(UUID userId) {
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 유저를 찾을 수 없습니다."));
        byte[] userProfileImgByte = binaryContentRepository.findBinaryContentByBinaryContentId(user.getProfileId()).get().getBytes();
        return new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), userProfileImgByte);
    }

    @Override
    public List<UserWithStatusResponseDto> findAllUserAndUserStatus() {
        List<UserResponseDto> users = findAllActiveUserDTO();
        List<UserWithStatusResponseDto> userList = new ArrayList<>();

        for (UserResponseDto user : users) {
            UserStatusResponseDto status = userStatusService.findUserStatusByUserId(user.getUserId());
            // 잘못 사용하면 순환참조 일어 날 수 있음
            Duration duration = Duration.between(status.getLoginTime(), Instant.now());
            String loginStatus = duration.toMinutes() < 5 ? "로그인" : "로그아웃";

            userList.add(new UserWithStatusResponseDto(user, loginStatus));
        }

        return userList;
    }

    @Override
    public List<UserDto> findAllUserDto(){
        List<User> users = userRepository.loadUsers();
        List<UserDto> userList = new ArrayList<>();

        for (User user : users) {
            UserStatusResponseDto status = userStatusService.findUserStatusByUserId(user.getId());
            Duration duration = Duration.between(status.getLoginTime(), Instant.now());
            Boolean loginStatus = duration.toMinutes() < 5;
            UserDto userDto = new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUserName(), user.getEmail(), user.getProfileId(), loginStatus);
            userList.add(userDto);
        }

        return userList;
    }
}
