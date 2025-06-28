package com.sprint.mission.discodeit.dto.UserStatusDto;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
        UUID id,
        Instant lastLoginTimes,
        UserStatusType userStatusType
) {}
