package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionManager sessionManager;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateRole(RoleUpdateRequest request) {
        return updateRoleInternal(request);
    }

    @Transactional
    @Override
    public UserDto updateRoleInternal(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        Role newRole = request.newRole();
        user.updateRole(newRole);

        sessionManager.invalidateSessionsByUserId(userId);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public JwtDto renewToken(String refreshTokenValue) {
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new RuntimeException("# Invalid refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("# 등록되지 않은 리프레시 토큰"));

        Map<String, Object> claims = jwtTokenProvider.getClaims(refreshTokenValue);
        UUID userId = UUID.fromString(claims.get("sub").toString());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        if (refreshToken.getRotated()) {
            log.debug("# 이미 사용된 리프래시 토큰, 재발급 요청");
            String newAccessToken = jwtTokenProvider.renewAccessToken(refreshToken.getToken());

            refreshToken.invalidate();
            refreshTokenRepository.save(refreshToken);
            RefreshToken newRefreshToken = refreshTokenService.saveRefreshToken(userId);
            log.info("# Refresh Token과 Access Token 재발급 완료, refreshToken = {}, accessToken = {}", newRefreshToken.getToken(), newAccessToken.getBytes());

            return new JwtDto(userMapper.toDto(user), newAccessToken);
        }


        String newAccessToken = jwtTokenProvider.renewAccessToken(refreshToken.getToken());

        refreshToken.invalidate();
        refreshTokenRepository.save(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.saveRefreshToken(userId);

        log.info("# Refresh Token과 Access Token 재발급 완료, refreshToken = {}, accessToken = {}", newRefreshToken.getToken(), newAccessToken.getBytes());
        return new JwtDto(userMapper.toDto(user), newAccessToken);
    }
}
