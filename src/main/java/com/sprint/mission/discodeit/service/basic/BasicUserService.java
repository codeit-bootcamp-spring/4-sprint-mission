package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    private UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId()
        );
    }

    private void updateUser(User user, String newUsername, String newEmail, String newPassword) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            user.setUsername(newUsername);
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            user.setEmail(newEmail);
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(user.getPassword())) {
            user.setPassword(newPassword);
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            user.setUpdatedAt(Instant.now());
        }
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new NoSuchElementException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new NoSuchElementException("Email already exists");
        }

        UUID profileId = null;
        if (request.profileImageData() != null && request.profileImageData().length > 0) {
            BinaryContent profile = new BinaryContent(request.profileImageData());
            profileId = binaryContentRepository.save(profile);
        }

        User user = new User(request);
        if (profileId != null) {
            user.setProfileId(profileId);
        }

        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user.getId()));

        return toDto(user);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        return toDto(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.userId() + " not found"));

        byte[] newImage = request.newProfileImageData();
        if (newImage != null && newImage.length > 0) {
            UUID oldProfileId = user.getProfileId();
            if (oldProfileId != null && binaryContentRepository.existsById(oldProfileId)) {
                binaryContentRepository.deleteById(oldProfileId);
            }

            BinaryContent newProfile = new BinaryContent(newImage);
            UUID newProfileId = binaryContentRepository.save(newProfile);
            user.setProfileId(newProfileId);
        }

        updateUser(user, request.newUsername(), request.newEmail(), request.newPassword());
        return toDto(userRepository.save(user));
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UUID profileId = user.getProfileId();
        if (profileId != null && binaryContentRepository.existsById(profileId)) {
            binaryContentRepository.deleteById(profileId);
        }

        if (userStatusRepository.existsByUserId(userId)) {
            userStatusRepository.deleteByUserId(userId);
        }

        userRepository.deleteById(userId);
    }
}
