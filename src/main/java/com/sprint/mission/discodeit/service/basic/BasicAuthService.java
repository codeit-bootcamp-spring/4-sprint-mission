package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginUserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto login(LoginUserDto loginUserDto) {
        User user = requireUserByUsername(loginUserDto.getUsername());
        validatePassword(loginUserDto.getPassword(), user.getPassword());

        // 로그인으로 인한 온라인 시간 갱신
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserStatus newUserStatus = new UserStatus(user.getId());
                    userStatusRepository.save(newUserStatus);
                    return newUserStatus;
                });
        userStatus.touch();
        userStatus.updateLastConnectedAt();

        return userMapper.toDto(user, userStatus.isOnline());
    }


    /* =========================================================
     * INTERNAL VALIDATION HELPERS
     * ========================================================= */
    /**
     * username을 기반으로 사용자를 조회합니다.
     * 존재하지 않으면 예외를 던집니다.
     *
     * @param username 조회할 사용자명
     * @return 조회된 사용자 엔티티
     * @throws IllegalArgumentException 사용자명이 존재하지 않는 경우
     */
    private User requireUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다: " + username));
    }

    /**
     * 입력한 비밀번호가 실제 저장된 비밀번호와 일치하는지 확인합니다.
     * 일치하지 않으면 예외를 던집니다.
     *
     * @param inputPassword 입력된 비밀번호
     * @param actualPassword DB에 저장된 실제 비밀번호
     * @throws IllegalArgumentException 비밀번호가 일치하지 않는 경우
     */
    private void validatePassword(String inputPassword, String actualPassword) {
        if (!actualPassword.equals(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
