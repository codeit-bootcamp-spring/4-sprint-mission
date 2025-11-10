package com.sprint.mission.discodeit.auth.registry;

import com.nimbusds.jose.JOSEException;

import java.util.UUID;

public interface JwtRegistry {
    public void registerJwtInformation(JwtInformation jwtInformation);

    public void invalidateJwtInformationByUserId(UUID userId);

    public boolean hasActiveJwtInformationByUserId(UUID userId);

    public boolean hasActiveJwtInformationByAccessToken(String accessToken);

    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation() throws JOSEException;
}
