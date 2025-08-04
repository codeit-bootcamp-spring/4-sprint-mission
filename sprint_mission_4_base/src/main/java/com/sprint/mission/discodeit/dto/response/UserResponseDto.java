package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto{
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private  UUID profileId;
    private Boolean online;
}
