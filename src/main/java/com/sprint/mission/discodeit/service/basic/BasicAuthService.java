package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserLoginDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private final UserMapper userMapper;

    @Override
    public UserStatusResponseDto login(UserLoginDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElse(new UserStatus(user.getId()));

        userStatus.setUpdatedAt(Instant.now());
        userStatusRepository.save(userStatus);

        return userMapper.toDto(user, userStatus);
    }
}
