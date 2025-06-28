package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthLoginDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository;

    public UserResponseDto login(AuthLoginDto authloginDto) {
        User matchedUser = examineMatchingUser(authloginDto);

        UserStatus matchedUserStatus = userStatusRepository.findByUserId(matchedUser.getId())
                .orElseThrow(() -> new NoSuchElementException("BasicAuthService.login().UserStatusRepository.findByUserId 실패: User not found"));

        matchedUserStatus.update(Instant.ofEpochSecond(Instant.now().getEpochSecond()));

        return userMapper.userToUserResponseDto(matchedUser, matchedUserStatus);
    }

    private User examineMatchingUser(AuthLoginDto authLoginDto) {
        return userRepository.findByUsername(authLoginDto.username())
                .filter(user -> user.getPassword().equals(authLoginDto.password()))
                .orElseThrow(() -> new NoSuchElementException("BasicAuthService.examineMatchingUser().User not found; 아이디나 패스워드가 잘못되었습니다."));
    }
}
