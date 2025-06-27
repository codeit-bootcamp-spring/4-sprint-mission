package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;

public interface AuthService {
    UserResponseDto logInUser(LoginRequestDto loginRequestDTO);
}
