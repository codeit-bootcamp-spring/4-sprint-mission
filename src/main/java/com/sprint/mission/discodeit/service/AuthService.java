package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserLoginDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;

public interface AuthService {

    UserStatusResponseDto login(UserLoginDto dto);
}
