package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserLoginResponseDto;


public interface AuthService {
    UserLoginResponseDto login(LoginRequest loginRequest);
}
