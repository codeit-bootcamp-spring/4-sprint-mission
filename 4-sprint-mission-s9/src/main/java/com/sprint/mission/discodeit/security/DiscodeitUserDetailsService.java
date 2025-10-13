package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)  // 필요 시 email도 함께 조회
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 비밀번호는 DB의 해시(예: BCrypt)를 그대로 전달
        String passwordHash = user.getPassword();

        UserDto userDto = userMapper.toDto(user); // Principal에 담을 확장 정보
        return new DiscodeitUserDetails(userDto, user, passwordHash);
    }
}
