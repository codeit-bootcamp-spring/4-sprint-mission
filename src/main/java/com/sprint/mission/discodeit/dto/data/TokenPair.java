package com.sprint.mission.discodeit.dto.data;

public record TokenPair(
        JwtDto jwtDto,
        String refreshToken
) {
}
