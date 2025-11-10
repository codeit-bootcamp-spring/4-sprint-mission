package com.sprint.mission.discodeit.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
