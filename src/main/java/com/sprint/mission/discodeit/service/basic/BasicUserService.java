package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor // NOTE 생성자 lombok에서 생성 final이 붙은 필드를 매개변수로 받아 생성자에서 주입
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentsRepository binaryContentsRepository;

    @Override
    public UserResponseDto createUser(UserCreateRequestDto userCreateRequestDTO) {
        final String DEFAULT_PROFILE_IMG_PATH = "./src/main/resources/static/basicUserProfileImage.png";

        String userName = userCreateRequestDTO.getUsername();
        String password = userCreateRequestDTO.getPassword();
        String userEmail = userCreateRequestDTO.getEmail();
        String profilePicturePath = Optional.ofNullable(userCreateRequestDTO.getProfileImagePath())
                .orElse(DEFAULT_PROFILE_IMG_PATH);

        validateUserNameNotDuplicated(userName);
        validateUserEmailNotDuplicated(userEmail);

        byte[] profileImageBytes;
        try (FileInputStream fis = new FileInputStream(profilePicturePath)) {
            profileImageBytes = fis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지를 읽는 데 실패했습니다: " + e.getMessage(), e);
        }

        BinaryContents profileImg = new BinaryContents(profilePicturePath, BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);

        User user = new User(userName, password, userEmail, profileImg.getId());
        userRepository.createUser(user);

        profileImg.setReferenceId(user.getId());
        binaryContentsRepository.createBinaryContents(profileImg);

        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                profileImageBytes // 프로필 이미지 바이트 직접 반환
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
            BinaryContents profileImg = new BinaryContents(userUpdateRequestDTO.getNewProfileImagePath(), BinaryContentType.USER_PROFILE_IMAGE, profileImageBytes);
            profileImg.setReferenceId(targetUser.getId());
            binaryContentsRepository.deleteBinaryContensByBinaryContentsId(targetUser.getProfileId());
            targetUser.setProfileId(profileImg.getId());
            binaryContentsRepository.createBinaryContents(profileImg);
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
        binaryContentsRepository.deleteBinaryContensByBinaryContentsId(user.getProfileId());
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
            userResponseDtos.add(new UserResponseDto(user.getId(), user.getUserName(), user.getEmail(), binaryContentsRepository.findBinaryContentsByBinaryContentsId(user.getProfileId()).get().getBinaryData()));
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
                        binaryContentsRepository
                                .findBinaryContentsByBinaryContentsId(user.getProfileId()).get()
                                .getBinaryData()
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
                        null //삭제된 유저이므로 사진이 없음
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
        Optional<BinaryContents> profileImgFromFile= binaryContentsRepository.findBinaryContentsByBinaryContentsId(user.getProfileId());

        if(profileImgFromFile.isEmpty()){
            throw new IllegalArgumentException("profileImg 정보가 없습니다.");
        }
        BinaryContents profileImg = profileImgFromFile.get();

        return profileImg.getBinaryContentsPath().equals(userUpdateRequestDTO.getNewProfileImagePath());
    }

    private User findUserByUserId(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저를 찾을 수 없습니다."));
    }
}
