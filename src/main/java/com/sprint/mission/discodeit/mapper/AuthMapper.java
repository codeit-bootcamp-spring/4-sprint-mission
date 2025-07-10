package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthMapper {

    final private UserStatusMapper userStatusMapper;

    // Request
    public AuthDto.LoginRequest toUserLoginRequestDto(String username, String password) {
        return new AuthDto.LoginRequest(
                username,
                password
        );
    }

    // Response
    public AuthDto.LoginResponse toLoginResponseDto(User user, UserStatus userStatus) {

        return new AuthDto.LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                userStatusMapper.toUserStatusResponseDto(userStatus)
        );
    }
}
