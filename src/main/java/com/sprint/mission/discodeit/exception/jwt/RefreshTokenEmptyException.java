package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class RefreshTokenEmptyException extends JwtException {
    public RefreshTokenEmptyException() {
        super(ErrorCode.REFRESH_TOKEN_EMPTY);
    }
    
    public static RefreshTokenEmptyException withId(UUID userId) {
        RefreshTokenEmptyException exception = new RefreshTokenEmptyException();
        exception.addDetail("userId", userId);
        return exception;
    }
    
    public static RefreshTokenEmptyException withUsername(String username) {
        RefreshTokenEmptyException exception = new RefreshTokenEmptyException();
        exception.addDetail("username", username);
        return exception;
    }
} 