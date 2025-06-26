package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.login.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public interface AuthService {
    public UserResponseDto login(LoginRequestDto loginRequestDto);
}
