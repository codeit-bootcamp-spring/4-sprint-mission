package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        UUID profileId,
        UserStatusType userStatusType,
        Instant lastLoginTimes
) {}
