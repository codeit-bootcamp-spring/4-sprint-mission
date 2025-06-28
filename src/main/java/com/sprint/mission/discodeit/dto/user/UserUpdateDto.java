package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;

import java.util.UUID;

public record UserUpdateDto(
        UUID id,
        String username,
        String email,
        String password,
        BinaryContentCreateDto binaryContentCreateDto
) {
}
