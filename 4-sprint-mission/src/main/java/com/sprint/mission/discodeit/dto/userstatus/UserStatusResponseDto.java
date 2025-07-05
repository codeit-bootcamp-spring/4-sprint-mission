package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(UUID id, UUID userId, boolean isOnline, Instant lastAccessAt) {}
