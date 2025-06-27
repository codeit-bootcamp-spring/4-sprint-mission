package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        userRepository.findByUsername(userCreateDto.getUsername())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already exists");
                });

        userRepository.findByEmail(userCreateDto.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists");
                });

        User user = userMapper.userCreateDtoToUser(userCreateDto);

        if (userCreateDto.getProfilePicture() != null) {
            BinaryContent profile = userMapper.binaryContentDtoToEntity(userCreateDto.getProfilePicture());
            binaryContentRepository.save(profile);
            user.setProfileId(profile.getId());
        }

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
        return userMapper.userToUserResponseDto(user, userStatus);
    }

    @Override
    public UserResponseDto findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);
        return userMapper.userToUserResponseDto(user, status);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .filter(user -> userStatusRepository.findByUserId(user.getId()).isPresent())
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).get();
                    return userMapper.userToUserResponseDto(user, userStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto update(UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userUpdateDto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userUpdateDto.getUserId() + " not found"));
        user.update(userUpdateDto.getUsername(), userUpdateDto.getEmail(), userUpdateDto.getPassword());

        if (userUpdateDto.getNewProfile() != null) {
            BinaryContent newProfile = userMapper.binaryContentDtoToEntity(userUpdateDto.getNewProfile());
            binaryContentRepository.save(newProfile);
            user.setProfileId(newProfile.getId());
        }

        userRepository.save(user);
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
        return userMapper.userToUserResponseDto(user, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userRepository.deleteById(userId);
        userStatusRepository.delete(userId);
    }
}
