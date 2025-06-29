package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.LoginRequest;
import com.sprint.mission.discodeit.DTO.LoginResponse;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
}
