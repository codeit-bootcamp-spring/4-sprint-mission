package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UUID profileId
) {}