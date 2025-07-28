package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserCreateRequest userCreateRequest,
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        BinaryContent profile = optionalProfileCreateRequest
                .map(req -> new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType(), req.bytes()))
                .map(binaryContentRepository::save)
                .orElse(null);

        User user = userMapper.toEntity(userCreateRequest);
        user.setProfile(profile);
        Instant now = Instant.now();
        //        UserStatus userStatus = new UserStatus(user, now);
//        userStatusRepository.save(userStatus); //영속성 전이로 인해 자동 저장
        user.setUserStatus(new UserStatus(user, now));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(userMapper.toDto(user));
        }
        return userDtos;
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (newEmail != null && (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail))) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (newUsername != null && (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername))) {
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        BinaryContent newProfile = user.getProfile(); // 기본값은 기존 프로필

        // 프로필 이미지 교체
        if (optionalProfileCreateRequest.isPresent()) {
            if (user.getProfile() != null) {
                binaryContentRepository.delete(user.getProfile());
            }
             newProfile = optionalProfileCreateRequest
                    .map(req -> new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType(), req.bytes()))
                    .map(binaryContentRepository::save)
                    .orElse(null);
        }
        user.update(newUsername, newEmail, userUpdateRequest.newPassword(), newProfile);
        //return userRepository.save(user); //이미 영속 상태이기 때문에 다시 save할 필요 없다.
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional.ofNullable(user.getProfile())
                .ifPresent(binaryContentRepository::delete);
        //userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
