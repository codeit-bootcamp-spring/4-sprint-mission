package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto.UserResponseDto;

public interface AuthService {

    UserResponseDto login(LoginRequest requestDto);
}
