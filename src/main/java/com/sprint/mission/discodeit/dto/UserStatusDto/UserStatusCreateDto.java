package com.sprint.mission.discodeit.dto.UserStatusDto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateDto(
        UUID userId,
        Instant lastLoginTimes
) {}
