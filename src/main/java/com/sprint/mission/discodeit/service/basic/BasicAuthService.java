package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginDto request) {
        String username = request.getUsername();
        String password = request.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password");
        }

        return user;
    }
}
