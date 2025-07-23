package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserCreateResponseDto(
        UUID id,
        String name,
        String email,
        UUID profile,
        UserStatusResponseDto userStatus
) {
}
