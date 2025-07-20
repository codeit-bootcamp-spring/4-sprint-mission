package com.sprint.mission.discodeit.dto.userstatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserStatusUpdateDto {
    private UUID id;
    private UUID userId;
    private Instant lastAccessedAt;
}
