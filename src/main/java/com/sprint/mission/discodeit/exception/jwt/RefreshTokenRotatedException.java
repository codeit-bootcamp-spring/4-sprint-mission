package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class RefreshTokenRotatedException extends JwtException {
    public RefreshTokenRotatedException() {
        super(ErrorCode.REFRESH_TOKEN_ROTATED);
    }
    
    public static RefreshTokenRotatedException withId(UUID userId) {
        RefreshTokenRotatedException exception = new RefreshTokenRotatedException();
        exception.addDetail("userId", userId);
        return exception;
    }
    
    public static RefreshTokenRotatedException withUsername(String username) {
        RefreshTokenRotatedException exception = new RefreshTokenRotatedException();
        exception.addDetail("username", username);
        return exception;
    }
} 