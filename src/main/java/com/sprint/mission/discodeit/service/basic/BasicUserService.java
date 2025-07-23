package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserCreateResponseDto createUser(UserCreateRequest userCreateRequest, BinaryContentPostDto binaryContentPostDto) {
        isDuplicateEmail(userCreateRequest.email());
        isDuplicateName(userCreateRequest.username());

        UUID profileId = null;
        if (binaryContentPostDto != null) {
            BinaryContent binaryContent = binaryContentMapper.toBinaryContent(binaryContentPostDto);
            profileId = binaryContent.getId();

            binaryContent.setActive(ActiveStatus.ACTIVE);
            binaryContentRepository.save(binaryContent);
        }

        if (profileId == null) System.out.println("이미지가 포함되지 않아 기본 프로필로 설정됩니다.");

        User user = userMapper.ofUser(userCreateRequest, profileId);
        user.setActive(ActiveStatus.ACTIVE);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now(), true);
        userStatusRepository.save(userStatus);

        UserStatusResponseDto userStatusResponseDto = userStatusMapper.toUserStatusResponseDto(userStatus);
        UserCreateResponseDto userResponseDto = userMapper.ofUserCreateResponseDto(user, userStatusResponseDto);
        return userResponseDto;
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        userRepository.findAll()
                .forEach(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
                    UserStatusResponseDto userStatusResponseDto = userStatusMapper.toUserStatusResponseDto(userStatus);
                    userResponseDtos.add(userMapper.ofUserResponseDto(user, userStatusResponseDto));
                });
        return userResponseDtos;
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> userDtos = new ArrayList<>();
        userRepository.findAll().stream()
                .forEach(user -> {
                    UserDto userDto = userMapper.toUserDto(user);
                    userDtos.add(userDto);
                });
        return userDtos;
    }

    @Override
    public UserResponseDto findUserById(UUID userId) {
        User user = userRepository.findById(userId);
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());
        UserStatusResponseDto userStatusResponseDto = userStatusMapper.toUserStatusResponseDto(userStatus);
        UserResponseDto userResponseDto = userMapper.ofUserResponseDto(user, userStatusResponseDto);
        return userResponseDto;
    }

    @Override
    public UserResponseDto getUserByName(String name) {
        User user = userRepository.findByName(name);
        UserStatus userStatus = userStatusRepository.findById(user.getId());
        UserStatusResponseDto userStatusResponseDto = userStatusMapper.toUserStatusResponseDto(userStatus);
        UserResponseDto userResponseDto = userMapper.ofUserResponseDto(user, userStatusResponseDto);
        return userResponseDto;
    }

    @Override
    public UserResponseDto updateUser(UUID userId, UserUpdateRequest userUpdateRequest, BinaryContentPostDto binaryContentPostDto) {
        User findUser = userRepository.findById(userId);

        isDuplicateName(userUpdateRequest.newUsername());
        isDuplicateEmail(userUpdateRequest.newEmail());

        Optional.ofNullable(userUpdateRequest.newUsername())
                .ifPresent(findUser::setUsername);
        Optional.ofNullable(userUpdateRequest.newPassword())
                .ifPresent(findUser::setPassword);
        Optional.ofNullable(userUpdateRequest.newEmail())
                .ifPresent(findUser::setEmail);
        Optional.ofNullable(binaryContentPostDto).ifPresent(dto -> {
            BinaryContent binaryContent = binaryContentMapper.toBinaryContent(dto);
            Optional.ofNullable(findUser.getProfileId()).ifPresent(binaryContentRepository::delete);
            binaryContent.setActive(ActiveStatus.ACTIVE);
            binaryContentRepository.save(binaryContent);
            findUser.setProfileId(binaryContent.getId());
        });
        Instant now = Instant.now();
        findUser.setUpdatedAt(now);
        UserStatus userStatus = userStatusRepository.findByUserId(findUser.getId());
        userStatus.update(now);
        userRepository.save(findUser);
        userStatusRepository.save(userStatus);

        UserStatusResponseDto userStatusResponseDto = userStatusMapper.toUserStatusResponseDto(userStatus);
        return userMapper.ofUserResponseDto(findUser, userStatusResponseDto);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId);
        validateActiveUser(user);

        // 프로필 이미지가 있는 경우에만 제거한다.
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
            user.setProfileId(null);
        }

        // 유저가 가진 채널과 정보 모두 삭제
        user.setActive(ActiveStatus.DELETE);
        userRepository.delete(user);

        userStatusRepository.deleteByUserId(user.getId());
    }

    private void validateActiveUser(User user) {
        if (!user.getActive().equals(ActiveStatus.ACTIVE))
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    // 동일한 이메일이 존재하는지 확인
    private void isDuplicateEmail(String email) {
        if (userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email)))
            throw new BusinessLogicException(ExceptionCode.EMAIL_OR_USERNAME_ALREADY_EXISTS);
    }

    private void isDuplicateName(String name) {
        if (userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(name)))
            throw new BusinessLogicException(ExceptionCode.EMAIL_OR_USERNAME_ALREADY_EXISTS);
    }
}
