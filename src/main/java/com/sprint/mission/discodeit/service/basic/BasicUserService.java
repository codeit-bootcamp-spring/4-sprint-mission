package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {

        // username 중복 검사
        if (detectUsernameIsDuplicated(userCreateDto.username())) {
            throw new IllegalArgumentException("BasicUserService.create() 실패: username is already taken");
        }

        // email 중복 검사
        if (detectEmailIsDuplicated(userCreateDto.email())) {
            throw new IllegalArgumentException("BasicUserService.create() 실패: email is already taken");
        }

        // 패스워드는 빈칸이 될 수 없음
        if (userCreateDto.password().trim().isEmpty()) {
            throw new IllegalArgumentException("BasicUserService.create() 실패: password is empty");
        }

        User createdUser = userMapper.userCreateDtoToUser(userCreateDto);

        if (userCreateDto.binaryContentCreateDto() != null
                && userCreateDto.binaryContentCreateDto().binaryContentType() == BinaryContentType.PROFILE) {
            createdUser.setProfileId(saveProfileImage(userCreateDto.binaryContentCreateDto(), createdUser.getId()).getId());
        }

        UserStatus userStatus = new UserStatus(createdUser.getId(), createdUser.getCreatedAt());
        userStatusRepository.save(userStatus);
        userRepository.save(createdUser);
        return userMapper.userToUserResponseDto(createdUser, userStatus);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return userMapper.userToUserResponseDto(user, userStatus);
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with name " + username + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + user.getId() + " not found"));

        return userMapper.userToUserResponseDto(user, userStatus);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        userRepository.findAll()
                .forEach(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("User with id " + user.getId() + " not found"));
                    userResponseDtos.add(userMapper.userToUserResponseDto(user, userStatus));
                });
        return userResponseDtos;
    }

    @Override
    public UserResponseDto update(UserUpdateDto userUpdateDto) {
        User updatedUser = userRepository.findById(userUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userUpdateDto.id() + " not found"));

        // OFFLINE 상태인 유저는 update를 할 수 없음.
        UserStatus userStatus = getUserStatusByUserId(updatedUser.getId());
        if (userStatus.checkUserOnline() == UserStatusType.OFFLINE) {
            throw new RuntimeException("User is not online");
        }

        // username 중복 검사
        if (detectUsernameIsDuplicated(userUpdateDto.username())) {
            throw new IllegalArgumentException("BasicUserService.update() 실패: username is already taken");
        }
        
        // email 중복 검사
        if (detectEmailIsDuplicated(userUpdateDto.email())) {
            throw new IllegalArgumentException("BasicUserService.update() 실패: email is already taken");
        }

        UUID newProfileId = updatedUser.getProfileId();

        if (userUpdateDto.binaryContentCreateDto() != null
                && userUpdateDto.binaryContentCreateDto().binaryContentType() == BinaryContentType.PROFILE) {
            newProfileId = (saveProfileImage(userUpdateDto.binaryContentCreateDto(), updatedUser.getId()).getId());
        }

        updatedUser.update(userUpdateDto.username(), userUpdateDto.email(), userUpdateDto.password(), newProfileId);
        userRepository.save(updatedUser);
        return userMapper.userToUserResponseDto(updatedUser, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        // user 유효성 검사 겸 불러오기
        User deletedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        
        // OFFLINE 상태인 유저는 탈퇴가 불가능함
        UserStatus userStatus = getUserStatusByUserId(userId);
        if (userStatus.checkUserOnline() == UserStatusType.OFFLINE) {
            System.out.println("BasicUserService.delete() 실패: User가 현재 online 상태가 아닙니다; 로그인 시간이 5분을 초과했습니다.");
        }

        userRepository.deleteById(userId);
        userStatusRepository.delete(userStatus.getId());
        binaryContentRepository.delete(deletedUser.getProfileId());
    }


    // 중복된 username이면 false를 리턴한다.
    private boolean detectUsernameIsDuplicated(String username) {
        for (User user : userRepository.findAll()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // 중복된 email이면 false를 리턴한다.
    private boolean detectEmailIsDuplicated(String email) {
        for (User user : userRepository.findAll()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private BinaryContent saveProfileImage(BinaryContentCreateDto dto, UUID userId) {
        BinaryContent createdBinaryContent = binaryContentMapper.binaryContentCreateDtoToBinaryContent(dto);
        createdBinaryContent.setUserId(userId);
        return binaryContentRepository.save(createdBinaryContent);
    }

    private UserStatus getUserStatusByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }
}
