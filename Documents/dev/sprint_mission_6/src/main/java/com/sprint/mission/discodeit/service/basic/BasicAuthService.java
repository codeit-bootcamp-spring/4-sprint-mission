package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authservice.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto login(AuthLoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NoSuchElementException("해당 이메일과 일치하는 유저가 없습니다."));
        if(!user.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        userRepository.save(user);

        return userMapper.userToUserDto(user);
    }
}
