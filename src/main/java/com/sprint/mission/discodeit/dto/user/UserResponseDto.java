package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private UUID profileId;
    private boolean isOnline;
    private Instant createdAt;
    private Instant updatedAt;
}
