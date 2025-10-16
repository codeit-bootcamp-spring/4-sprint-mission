package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class RefreshTokenExpiredException extends JwtException {
    public RefreshTokenExpiredException() {
        super(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }
    
    public static RefreshTokenExpiredException withId(UUID userId) {
        RefreshTokenExpiredException exception = new RefreshTokenExpiredException();
        exception.addDetail("userId", userId);
        return exception;
    }
    
    public static RefreshTokenExpiredException withUsername(String username) {
        RefreshTokenExpiredException exception = new RefreshTokenExpiredException();
        exception.addDetail("username", username);
        return exception;
    }
} 