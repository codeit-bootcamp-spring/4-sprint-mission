package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    @Autowired(required = false)
    private BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public User createUser(UserCreateRequest userCreateRequest, UUID profileId) {
        isDuplicateEmail(userCreateRequest.email());
        isDuplicateName(userCreateRequest.username());

        if (profileId == null) {
            System.out.println("이미지가 포함되지 않아 기본 프로필로 설정됩니다.");
        }
        BinaryContent profile = Optional.ofNullable(profileId)
                .flatMap(binaryContentRepository::findById)
                .orElse(null);
        User user = new User(userCreateRequest.username(), userCreateRequest.password(), userCreateRequest.email(), profile);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User updateUser(UUID userId, UserUpdateRequest userUpdateRequest, UUID newProfileId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK>."));

        isDuplicateName(userUpdateRequest.newUsername());
        isDuplicateEmail(userUpdateRequest.newEmail());

        Optional.ofNullable(userUpdateRequest.newUsername())  // username 업데이트
                .ifPresent(findUser::setUsername);
        Optional.ofNullable(userUpdateRequest.newPassword())  // password 업데이트
                .ifPresent(findUser::setPassword);
        Optional.ofNullable(userUpdateRequest.newEmail())  // email 업데이트
                .ifPresent(findUser::setEmail);
        Optional.ofNullable(newProfileId).ifPresent(binaryContentId -> {  // 변경할 프로필이 있으면 삭제 후 등록
            Optional.ofNullable(findUser.getProfile()).ifPresent(binaryContentRepository::delete);
            BinaryContent binaryContent = binaryContentRepository.findById(newProfileId)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND));
            binaryContentRepository.save(binaryContent);
            findUser.setProfile(binaryContent);
        });
        UserStatus userStatus = userStatusRepository.findByUserId(findUser.getId());
        userStatus.update(Instant.now());
        userRepository.save(findUser);
        userStatusRepository.save(userStatus);

        return findUser;
    }

    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        validateActiveUser(user);

        // 프로필 이미지가 있는 경우에만 제거한다.
        if (user.getProfile() != null) {
            binaryContentRepository.delete(user.getProfile());
            user.setProfile(null);
        }

        userRepository.delete(user);

        userStatusRepository.deleteByUserId(user.getId());
    }

    private void validateActiveUser(User user) {
        if (!userRepository.existsById(user.getId())) throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    // 동일한 이메일이 존재하는지 확인
    private void isDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new BusinessLogicException(ExceptionCode.EMAIL_OR_USERNAME_ALREADY_EXISTS);
    }

    private void isDuplicateName(String name) {
        if (userRepository.existsByUsername(name))
            throw new BusinessLogicException(ExceptionCode.EMAIL_OR_USERNAME_ALREADY_EXISTS);

    }
}
