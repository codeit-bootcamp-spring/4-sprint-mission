package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtProvider jwtProvider;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {

        Cookie refreshTokenExpirationCookie = jwtProvider.createRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenExpirationCookie);
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(JwtProvider.REFRESH_TOKEN_COOKIE_NAME))
                    .findFirst()
                    .ifPresent(cookie -> {
                        try {
                            SignedJWT signedJWT = SignedJWT.parse(cookie.getValue());
                            String userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");
                            jwtRegistry.invalidateJwtInformationByUserId(UUID.fromString(userId));
                        } catch (Exception e) {
                            log.warn("Failed to invalidate JWT information on logout", e);
                        }
                    });
        }
        Cache userCache = cacheManager.getCache("users");
        if(userCache != null) {
            userCache.clear();
        }
        log.debug("JWT 로그아웃 핸들러 실행 - 리프레시 토큰 쿠키 삭제");
    }
}
