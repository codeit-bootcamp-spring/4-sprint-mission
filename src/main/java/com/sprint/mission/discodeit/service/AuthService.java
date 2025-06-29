package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthRequestDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;

import java.util.UUID;

public interface AuthService {
    UserResponseDto login(AuthRequestDto authRequestDto);
    void logout(UUID uuid);
}
