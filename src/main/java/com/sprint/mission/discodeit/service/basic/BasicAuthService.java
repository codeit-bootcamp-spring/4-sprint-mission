package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.LoginRequest;
import com.sprint.mission.discodeit.DTO.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest)
    {
        User user =  userRepository.findAll().stream()
                .filter(u ->
                        u.getUsername().equals(loginRequest.username()) &&
                        u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("로그인 실패, 입력값과 일치하는 유저가 없습니다"));

        return new LoginResponse(user.getUsername(),user.getEmail(),user.getId());

    }
}
