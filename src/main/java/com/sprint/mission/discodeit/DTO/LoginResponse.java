package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record LoginResponse(
        String username,
        String email,
        UUID userId
) {}
