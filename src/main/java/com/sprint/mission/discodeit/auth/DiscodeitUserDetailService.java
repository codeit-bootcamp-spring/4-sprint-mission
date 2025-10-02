package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.utils.DiscodeitAuthorityUtils;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DiscodeitAuthorityUtils authorityUtils;

    @Override
    public DiscodeitUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        UserDto userDto = userMapper.toDto(user, false); // 로딩중이므로 online 초기값: false
        return new DiscodeitUserDetails(userDto, user.getPassword(), authorityUtils);
    }
}
