package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto requestDto);
}
