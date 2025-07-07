package com.sprint.mission.discodeit.dto.userStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class UserStatusResponseDto {
    private final UUID id;
    private final UUID userId;
    private final Instant lastConnectedAt;
}
