package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class RefreshTokenNotFoundException extends JwtException {
    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
    
    public static RefreshTokenNotFoundException withId(UUID userId) {
        RefreshTokenNotFoundException exception = new RefreshTokenNotFoundException();
        exception.addDetail("userId", userId);
        return exception;
    }
    
    public static RefreshTokenNotFoundException withUsername(String username) {
        RefreshTokenNotFoundException exception = new RefreshTokenNotFoundException();
        exception.addDetail("username", username);
        return exception;
    }

    public static RefreshTokenNotFoundException withToken(String tokenValue) {
        RefreshTokenNotFoundException exception = new RefreshTokenNotFoundException();
        exception.addDetail("token", tokenValue);
        return exception;
    }
}