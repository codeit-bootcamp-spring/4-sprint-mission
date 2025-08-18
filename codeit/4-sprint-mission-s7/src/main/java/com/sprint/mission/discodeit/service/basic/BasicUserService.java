package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        log.info("Create User requested: username='{}', profilePresent={}",
                username, optionalProfileCreateRequest.isPresent());

        if (userRepository.existsByEmail(email)) {
            DuplicateEmailException duplicateEmailException = new DuplicateEmailException(email);
            log.debug("Create User rejected: duplicate email: email='{}'", email, duplicateEmailException);
            throw duplicateEmailException;
        }
        if (userRepository.existsByUsername(username)) {
            DuplicateUsernameException duplicateUsernameException = new DuplicateUsernameException(username);
            log.debug("Create User rejected: duplicate username: username='{}'", username, duplicateUsernameException);
            throw duplicateUsernameException;
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);

        log.info("Create User succeeded: id={}, username='{}'", user.getId(), username);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAllWithProfileAndStatus()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        log.info("Update User requested: id={}, profilePresent={}",
                userId, optionalProfileCreateRequest.isPresent());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    UserNotFoundException userNotFoundException = new UserNotFoundException(userId);
                    log.debug("Update User rejected: not found: id={}", userId, userNotFoundException);
                    return userNotFoundException;
                });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (userRepository.existsByEmail(newEmail)) {
            DuplicateEmailException duplicateEmailException = new DuplicateEmailException(newEmail);
            log.debug("Update User rejected: duplicate email: id={}, email='{}'", userId, newEmail, duplicateEmailException);
            throw duplicateEmailException;
        }
        if (userRepository.existsByUsername(newUsername)) {
            DuplicateUsernameException duplicateUsernameException = new DuplicateUsernameException(newUsername);
            log.debug("Update User rejected: duplicate username: id={}, username='{}'", userId, newUsername, duplicateUsernameException);
            throw duplicateUsernameException;
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        log.info("Update User succeeded: id={}", userId);

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {

        log.info("Delete User requested: id={}", userId);

        if (!userRepository.existsById(userId)) {
            UserNotFoundException userNotFoundException = new UserNotFoundException(userId);
            log.debug("Delete User rejected: not found: id={}", userId, userNotFoundException);
            throw userNotFoundException;
        }

        userRepository.deleteById(userId);

        log.info("Delete User succeeded: id={}", userId);
    }
}
