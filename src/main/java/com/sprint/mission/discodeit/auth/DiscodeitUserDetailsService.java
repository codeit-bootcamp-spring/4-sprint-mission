package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * UserDetailsService 컴포넌트 대체
 *
 * UserDetailsService: username을 기반으로 사용자 세부 정보(UserDetails)를 DB 등에서 불러오는 역할
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscodeitUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    private final UserMapper userMapper;

    @Override
    public DiscodeitUserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 특정 사용자 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withUsername(username));

        Boolean isOnline = sessionRegistry.getAllPrincipals().contains(user);

        UserDto userDto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                userMapper.toDto(user).profile(),
                isOnline,
                user.getRole()
        );

        return new DiscodeitUserDetails(userDto, user.getPassword());
    }
}
