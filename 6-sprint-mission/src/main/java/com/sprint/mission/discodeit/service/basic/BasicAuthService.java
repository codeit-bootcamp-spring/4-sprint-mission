package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.login.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.WrongPasswordException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public UserDto login(LoginRequest loginRequest) {
        User user = authenticate(loginRequest);
        updateLastAccessTime(user.getId());

        BinaryContentDto binaryContentDto = toBinaryContentDto(user.getProfile());
        return userMapper.userToUserDto(user, binaryContentDto);
    }

    private User authenticate(LoginRequest loginRequest) {
        User user =
                userRepository
                        .findByUsername(loginRequest.username())
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                "User with username " + loginRequest.username() + " not found"));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new WrongPasswordException("Wrong password");
        }
        return user;
    }

    private void updateLastAccessTime(UUID userId) {
        userStatusRepository
                .findByUserId(userId)
                .ifPresent(
                        userStatus -> {
                            userStatus.updateAccessTime();
                            userStatusRepository.save(userStatus);
                        });
    }

    private BinaryContentDto toBinaryContentDto(BinaryContent binaryContent) {
        if (binaryContent == null) return null;

        try (InputStream inputStream = binaryContentStorage.get(binaryContent.getId())) {
            byte[] bytes = inputStream.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent, base64);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 로딩 실패", e);
        }
    }
}
