package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserLoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserLoginResponseDto login(LoginRequest loginRequest) {
        if (loginRequest.password() == null || loginRequest.username() == null)
            throw new IllegalArgumentException("Username or Password is null");

        if (userRepository.findAll().stream()
                .noneMatch(user -> user.getUsername().equals(loginRequest.username())))
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);

        User finduser = userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(loginRequest.username())
                        && user.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.WRONG_PASSWORD));
        finduser.setUpdatedAt(Instant.now());
        return userMapper.toUserLoginResponseDto(finduser);
    }
}
