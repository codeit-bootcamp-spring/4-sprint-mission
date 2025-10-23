package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.service.RefreshTokenService;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
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

  @Transactional
  @Override
  public JwtDto reissueToken(String refreshToken) {

    if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
      throw new AuthenticationServiceException("유효하지 않은 리프레시 토큰입니다.");
    }

    //subject (userId) 추출
    String subject = jwtTokenProvider.getSubject(refreshToken);
    UUID userId = UUID.fromString(subject);

    //DB에서 토큰 조회 및 회전 처리
    RefreshToken token = refreshTokenService.findByUserId(userId);
    refreshTokenService.rotateRefreshToken(token);

    // 사용자 정보 로드
    User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));
    Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);

    // 새 토큰 발급
    String newAccessToken = jwtTokenProvider.generateAccessToken(claims, subject);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(claims, subject);

    return new JwtDto(userMapper.toDto(user), newAccessToken, newRefreshToken);
  }
}
