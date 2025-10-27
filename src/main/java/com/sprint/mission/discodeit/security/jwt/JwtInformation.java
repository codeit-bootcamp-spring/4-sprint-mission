package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtInformation {

    private final UserResponse userResponse;

    private final String accessToken;

    private final String refreshToken;

    public JwtInformation rotate(String accessToken, String refreshToken) {
        return new JwtInformation(userResponse, accessToken, refreshToken);
    }
}
