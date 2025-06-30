package com.sprint.mission.discodeit.dto.userStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserStatusResponseDto {
    UUID id;
    UUID userId;
    Instant lastConnectedAt;
}
