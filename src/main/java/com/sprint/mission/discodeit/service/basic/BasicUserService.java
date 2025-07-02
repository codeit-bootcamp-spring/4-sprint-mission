package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.sprint.mission.discodeit.util.UpdateUtil.updateIfChanged;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserStatusResponseDto create(UserCreateDto userCreateDto) {

        validateUserDuplications(userCreateDto.getUsername(), userCreateDto.getEmail());

        User createdUser = userMapper.toEntity(userCreateDto);
        if (userCreateDto.getBinaryContent() != null) {
            createdUser.setProfileImageId(
                    saveProfileImage(userCreateDto.getBinaryContent(), createdUser.getId()).getId()
            );
        }

        User savedUser = userRepository.save(createdUser);
        userStatusRepository.save(new UserStatus(savedUser.getId()));

        UserStatus userStatus = getUserStatusOrThrow(savedUser.getId());

        return userMapper.toDto(savedUser, userStatus);
    }

    @Override
    public UserStatusResponseDto find(UUID userId) {

        User user = getUserOrThrow(userId);
        UserStatus userStatus = getUserStatusOrThrow(userId);

        return userMapper.toDto(user, userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {

        List<User> users = userRepository.findAll();
        List<UserStatusResponseDto> userStatusDtos = new ArrayList<>();

        for (User user : users) {
            UserStatus userStatus = getUserStatusOrThrow(user.getId());
            userStatusDtos.add(userMapper.toDto(user, userStatus));
        }

        return userStatusDtos;
    }

    @Override
    public User update(UserUpdateDto userUpdateDto) {

        User user = getUserOrThrow(userUpdateDto.getId());
        boolean anyValueUpdated = false;

        anyValueUpdated = updateIfChanged(userUpdateDto.getUsername(), user.getUsername(), user::setUsername, anyValueUpdated);
        anyValueUpdated = updateIfChanged(userUpdateDto.getEmail(), user.getEmail(), user::setEmail, anyValueUpdated);
        anyValueUpdated = updateIfChanged(userUpdateDto.getPassword(), user.getPassword(), user::setPassword, anyValueUpdated);
        anyValueUpdated = Optional.ofNullable(userUpdateDto.getNewProfileImage())
                .map(mewImg -> {
                    Optional.ofNullable(user.getProfileImageId())
                            .ifPresent(binaryContentRepository::deleteById);

                    UUID newProfileImageId = saveProfileImage(mewImg, user.getId()).getId();
                    user.setProfileImageId(newProfileImageId);
                    return true;
                }).orElse(anyValueUpdated);

        if (anyValueUpdated) {
            user.setUpdatedAt(Instant.now());
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        binaryContentRepository.findAll().stream()
                .filter(bcr -> userId.equals(bcr.getUserId()))
                .forEach(bcr -> binaryContentRepository.deleteById(bcr.getId()));
        userStatusRepository.deleteById(userId);
        userRepository.deleteById(userId);
    }

    // 유저가 현재 접속 중인지 판단 -> UserStatus의 updatedAt이 현재로부터 5분 이내면 접속 중
    @Override
    public boolean isOnline(UserStatusResponseDto userStatusDto) {

        Instant fiveMinutesAgo = Instant.now().minusSeconds(60 * 5);

        return fiveMinutesAgo.isBefore(userStatusDto.getUpdatedAt());
    }

    // 프로필 이미지 등록
    private BinaryContent saveProfileImage(BinaryContentCreateDto binaryContentCreateDto, UUID userId) {
        BinaryContent createdBinaryContent = binaryContentMapper.toEntity(binaryContentCreateDto, userId);
        return binaryContentRepository.save(createdBinaryContent);
    }

    // 리팩토링 : 중복 제거용 private 메서드
    private User getUserOrThrow(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    private UserStatus getUserStatusOrThrow(UUID userId) {

        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with user id " + userId + " not found"));
    }

    private void validateUserDuplications(String username, String email) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("user 이름 중복");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("user email 중복");
        }
    }
}