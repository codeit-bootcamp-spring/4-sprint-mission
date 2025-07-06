package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserDto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    public UserResponseDto create(UserCreateDto dto, @Nullable BinaryContentCreateDto binaryDto) {
        userRepository.findByUsername(dto.getUsername())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already exists");
                });

        userRepository.findByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists");
                });

        User user = userMapper.userCreateDtoToUser(dto);

        if (binaryDto != null) {
            MultipartFile file = binaryDto.getFile();
            if (file != null && !file.isEmpty()) {
                try {
                    BinaryContent content = new BinaryContent(user.getId(), null,
                            dto.getProfile().getBytes(),
                            dto.getProfile().getOriginalFilename(),
                            dto.getProfile().getContentType());
                    binaryContentRepository.save(content);

                    user.setProfileId(content.getId());     // User에 연결
                } catch (IOException e) {
                    throw new RuntimeException("Failed to process profile image", e);
                }
            }
        }

        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

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
