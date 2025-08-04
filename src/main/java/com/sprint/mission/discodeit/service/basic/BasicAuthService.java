package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        if (loginRequest.password() == null || loginRequest.username() == null)
            throw new IllegalArgumentException("Username or Password is null");

        if (!userRepository.existsByUsername(loginRequest.username()))
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        User findUser = userRepository.findByUsername(loginRequest.username());
        if (!findUser.getPassword().equals(loginRequest.password()))
            throw new BusinessLogicException(ExceptionCode.WRONG_PASSWORD);

        return findUser;
    }
}
