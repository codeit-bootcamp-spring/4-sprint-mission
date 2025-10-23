package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(name = "UserStatusUpdateRequest")
public class UserStatusUpdateDto {
    private UUID id;
    private UUID userId;
    private Instant newLastActiveAt;
}
