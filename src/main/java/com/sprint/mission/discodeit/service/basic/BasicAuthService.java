package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAuthException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        if (!userRepository.existsByUsername(loginRequest.username()))
            throw new UserAuthException(ErrorCode.USER_NOT_FOUND, Map.of("username", loginRequest.username()));
        User findUser = userRepository.findByUsername(loginRequest.username());
        if (!findUser.getPassword().equals(loginRequest.password()))
            throw new UserAuthException(ErrorCode.WRONG_PASSWORD, Map.of("userId", findUser.getId(), "password", "Wrong Password"));

        return findUser;
    }
}
