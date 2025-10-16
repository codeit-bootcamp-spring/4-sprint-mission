package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.TokenResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    @PreAuthorize("hasRole=('ADMIN')")
    public User updateRole(RoleUpdateRequest roleUpdateRequest) {
        User user = getValidUser(roleUpdateRequest.userId());
        log.info("인가 권한 변경 id = {}, oldRole = {}, newRole = {}", roleUpdateRequest.userId(), user.getRole(), roleUpdateRequest.newRole());
        user.updateRole(roleUpdateRequest.newRole());
        sessionRegistry.getAllPrincipals()
                .forEach(principal -> {
                    if (principal instanceof DiscodeitUserDetails) {
                        DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) principal;
                        if (discodeitUserDetails.getUserDto().id().equals(user.getId())) {
                            List<SessionInformation> info = sessionRegistry.getAllSessions(principal, false);
                            info.forEach(sessionInformation -> {
                                log.debug("세션 만료 sessionId = {}", sessionInformation.getSessionId());
                                sessionInformation.expireNow();
//                                sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                            });
                            log.debug("세션 {}개 무효화", info.size());
                        }
                    }
                });
        return userRepository.save(user);
    }

    @Override
    public TokenResponse reissueToken(Map<String, Object> claims) {
        log.info("AccessToken 재발급 시도");
        Date expiredAt = (Date) claims.get("exp");
        String userEmail = (String) claims.get("sub");
        User user = getValidUserByEmail(userEmail);
        if (expiredAt.before(new Date())) {
            log.warn("RefreshToken 유효기간이 만료 됨");
            throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN, Map.of("expiredAt", expiredAt));
        }

        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("roles", user.getRole());
        accessClaims.put("username", user.getUsername());
        String newAccessToken = jwtTokenProvider.generateAccessToken(accessClaims, userEmail);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userEmail);

        TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
        log.info("AccessToken 및 RefreshToken 재발급 완료");
        return tokenResponse;
    }

    private User getValidUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("해당 유저를 찾을 수 없음 id = {}", userId);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
        });
    }

    private User getValidUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("해당 유저를 찾을 수 없음 email = {}", email);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("email", email));
        });
    }
}
