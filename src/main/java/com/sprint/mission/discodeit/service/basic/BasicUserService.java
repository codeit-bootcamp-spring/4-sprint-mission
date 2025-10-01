package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binary_content.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public User createUser(UserCreateRequest userCreateRequest, UUID profileId) {
        log.debug("유저 생성 호출");
        validateEmail(userCreateRequest.email());
        validateUsername(userCreateRequest.username());

        if (profileId == null) {
            System.out.println("이미지가 포함되지 않아 기본 프로필로 설정됩니다.");
        }
        BinaryContent profile = Optional.ofNullable(profileId)
                .flatMap(binaryContentRepository::findById)
                .orElse(null);
        String password = passwordEncoder.encode(userCreateRequest.password());
        User user = new User(userCreateRequest.username(), password, userCreateRequest.email(), profile);
        userRepository.save(user);
        log.info("유저 생성 완료 id = {}", user.getId());
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal.userDto.id")
    @Override
    public User updateUser(UUID userId, UserUpdateRequest userUpdateRequest, UUID newProfileId) {
        log.debug("유저 수정 호출");
        User findUser = getValidUser(userId);

        validateUpdateName(findUser.getUsername(), userUpdateRequest.newUsername());
        validateUpdateEmail(findUser.getEmail(), userUpdateRequest.newEmail());

        Optional.ofNullable(userUpdateRequest.newUsername())  // username 업데이트
                .ifPresent(findUser::setUsername);
        Optional.ofNullable(userUpdateRequest.newPassword())  // password 업데이트
                .ifPresent(password -> {
                    String encodePassword = passwordEncoder.encode(password);
                    findUser.setPassword(encodePassword);
                });
        Optional.ofNullable(userUpdateRequest.newEmail())  // email 업데이트
                .ifPresent(findUser::setEmail);
        Optional.ofNullable(newProfileId).ifPresent(binaryContentId -> {  // 변경할 프로필이 있으면 삭제 후 등록
            Optional.ofNullable(findUser.getProfile()).ifPresent(binaryContentRepository::delete);
            BinaryContent binaryContent = binaryContentRepository.findById(newProfileId)
                    .orElseThrow(() -> {
                        log.warn("프로필 이미지를 찾을 수 없음 id = {}", newProfileId);
                        throw new BinaryContentNotFoundException(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("newProfileId", newProfileId));
                    });
            binaryContentRepository.save(binaryContent);
            findUser.setProfile(binaryContent);
        });
        userRepository.save(findUser);
        log.info("유저 수정 완료 id = {}", findUser.getId());
        return findUser;
    }

    @Transactional
    @Override
    @PreAuthorize("#userId == authentication.principal.userDto.id")
    public void deleteUser(UUID userId) {
        log.debug("유저 삭제 호출 id = {}", userId);
        User user = getValidUser(userId);

        // 프로필 이미지가 있는 경우에만 제거한다.
        if (user.getProfile() != null) {
            binaryContentRepository.delete(user.getProfile());
            user.setProfile(null);
        }

        userRepository.delete(user);

        log.info("유저 삭제 완료 id = {}", userId);
    }

    @Override
    public User findById(UUID userId) {
        return getValidUser(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private User getValidUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("해당 유저를 찾을 수 없음 id = {}", userId);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
        });
    }

    // 동일한 이메일이 존재하는지 확인
    private void validateUpdateEmail(String oldEmail, String newEmail) {
        if (userRepository.existsByEmail(newEmail) && !oldEmail.equals(newEmail)) {
            log.warn("이메일 검증 실패 , 해당 이메일은 이미 존재합니다. email = {}", newEmail);
            throw new UserAlreadyExistsException(ErrorCode.EMAIL_OR_USERNAME_ALREADY_EXISTS, Map.of("email", newEmail));
        }
    }

    private void validateUpdateName(String oldName, String newName) {
        if (userRepository.existsByUsername(newName) && !oldName.equals(newName)) {
            log.warn("이름 검증 실패 name , 해당 username은 이미 존재합니다 = {}", newName);
            throw new UserAlreadyExistsException(ErrorCode.EMAIL_OR_USERNAME_ALREADY_EXISTS, Map.of("name", newName));
        }

    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("이메일 검증 실패, 해당 이메일은 이미 존재합니다. email = {}", email);
            throw new UserAlreadyExistsException(ErrorCode.EMAIL_OR_USERNAME_ALREADY_EXISTS, Map.of("email", email));
        }
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            log.warn("이름 검증 실패, 해당 username은 이미 존재합니다. username = {}", username);
            throw new UserAlreadyExistsException(ErrorCode.EMAIL_OR_USERNAME_ALREADY_EXISTS, Map.of("username", username));
        }
    }
}
