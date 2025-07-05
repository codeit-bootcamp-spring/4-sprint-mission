package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.login.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDto login(LoginRequestDto loginRequestDto) {
        User user = authenticate(loginRequestDto);
        updateLastAccessTime(user.getId());
        boolean isOnline = isUserOnline(user.getId());
        return userMapper.userToUserResponseDto(user, isOnline);
    }

    private User authenticate(LoginRequestDto loginRequestDto) {
        return userRepository.findAll().stream()
                .filter(
                        user ->
                                user.getUserName().equals(loginRequestDto.userName())
                                        && user.getUserPassword().equals(loginRequestDto.password()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    private void updateLastAccessTime(UUID userId) {
        userStatusRepository
                .findById(userId)
                .ifPresent(
                        userStatus -> {
                            userStatus.updateAccessTime();
                            userStatusRepository.save(userStatus);
                        });
    }

    private boolean isUserOnline(UUID userId) {
        return userStatusRepository.findById(userId).map(UserStatus::isOnline).orElse(false);
    }
}
