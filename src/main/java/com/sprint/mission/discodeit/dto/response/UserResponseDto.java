package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        BinaryContent profile,
        Boolean online
) {
}