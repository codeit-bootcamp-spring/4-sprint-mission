package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto.*;
import com.sprint.mission.discodeit.dto.UserDto.UserUpdateRequest;
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
    public UserResponseDto create(UserCreateRequest request, @Nullable MultipartFile file) {
        String email = request.email();
        String username = request.username();

        // 중복 검사
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        // 사용자 생성
        User user = new User(username, email, request.password());
        userRepository.save(user);

        // 프로필 이미지 저장
        if (file != null && !file.isEmpty()) {
            try {
                BinaryContent content = new BinaryContent(
                        user.getId(),
                        null,
                        file.getBytes(),
                        file.getOriginalFilename(),
                        file.getContentType()
                );
                binaryContentRepository.save(content);
                user.setProfileId(content.getId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile image", e);
            }
        }

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        userRepository.save(user);

        // 응답 생성
        return userMapper.toUserResponse(user);
    }

    /*@Override
    public UserDto findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);
        return userMapper.toUserResponse(user, status);
    }*/

    @Override
    public List<AllUserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    Optional<UserStatus> optionalStatus = userStatusRepository.findByUserId(user.getId());
                    UserStatus userStatus = optionalStatus.orElse(null); // null이면 offline 처리됨
                    return userMapper.toAllUserResponesDto(user, userStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserUpdateResponse update(UUID userId, UserUpdateRequest updateDto, @Nullable MultipartFile profile) {
        // 1. 기존 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 2. 중복 검사
        userRepository.findByUsername(updateDto.newUsername())
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("User with username " + updateDto.newUsername() + " already exists");
                });

        userRepository.findByEmail(updateDto.newEmail())
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("User with email " + updateDto.newEmail() + " already exists");
                });

        // 사용자 정보 수정
        user.update(updateDto.newUsername(), updateDto.newEmail(), updateDto.newPassword());

        // 프로필 이미지 교체
        if (profile != null && !profile.isEmpty()) {
            // 기존 프로필 삭제
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }
            // 새 파일 저장
            try {
                BinaryContent content = new BinaryContent(
                        user.getId(),
                        null,
                        profile.getBytes(),
                        profile.getOriginalFilename(),
                        profile.getContentType()
                );
                binaryContentRepository.save(content);
                user.setProfileId(content.getId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process profile image", e);
            }
        }

        userRepository.save(user);

        return userMapper.toUserUpdateResponse(user);
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
