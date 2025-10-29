package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.constant.AuthErrorCode;
import com.sprint.mission.discodeit.constant.TokenErrorCode;
import com.sprint.mission.discodeit.exception.TokenException;
import com.sprint.mission.discodeit.exception.UserAuthException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
            throw new UserAuthException(AuthErrorCode.INVALID_USER);
        }

        try {
            String accessToken = jwtProvider.createAccessToken(userDetails);
            String refreshToken = jwtProvider.createRefreshToken(userDetails);

            Cookie refreshCookie = jwtProvider.createRefreshTokenCookie(refreshToken);
            response.addCookie(refreshCookie);

            JwtDto jwtDto = new JwtDto(
                    userDetails.getUserResponse(),
                    accessToken
            );

            JwtInformation jwtInformation = new JwtInformation(
                    userDetails.getUserResponse(),
                    accessToken,
                    refreshToken
            );
            jwtRegistry.registerJwtInformation(jwtInformation);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), jwtDto);

            Cache userCache = cacheManager.getCache("users");
            if(userCache != null) {
                userCache.clear();
            }

            log.debug("JWT 토큰이 발급 사용자: {}", userDetails.getUsername());

        } catch (JOSEException e) {
            log.error("JWT 토큰 생성 실패: 사용자 {}", userDetails.getUsername(), e);
            throw new TokenException(TokenErrorCode.TOKEN_CREATION_ERROR);
        }
    }
}
