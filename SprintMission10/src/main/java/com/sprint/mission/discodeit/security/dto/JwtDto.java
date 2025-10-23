package com.sprint.mission.discodeit.security.dto;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) {
}
