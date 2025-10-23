package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class SecurityNotFoundException extends SecurityException {
    public SecurityNotFoundException() {
        super(ErrorCode.SECURITY_NOT_FOUND);
    }

    public static SecurityNotFoundException withAccessToken(String accessToken) {
        SecurityNotFoundException exception = new SecurityNotFoundException();
        exception.addDetail("accessToken", accessToken);
        return exception;
    }

    public static SecurityNotFoundException withRefreshToken(String refreshToken) {
        SecurityNotFoundException exception = new SecurityNotFoundException();
        exception.addDetail("refreshToken", refreshToken);
        return exception;
    }
}
