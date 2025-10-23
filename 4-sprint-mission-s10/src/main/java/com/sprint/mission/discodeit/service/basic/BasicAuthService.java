package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.utils.cache.RefreshResult;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionManager sessionManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

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
  public JwtDto refreshAccessToken(String refreshToken) {
    try {
      Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken); // 서명/만료 검증 포함(실패 시 예외)
      Object sub = claims.get("sub");
      if (sub == null) {
        throw new InvalidRefreshTokenException("Invalid refresh token: subject missing");
      }
      String username = sub.toString();

      // 2) 사용자 로드 → roles 클레임 구성
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      List<String> roles = userDetails.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .toList();
      Map<String, Object> newAccessClaims = Map.of("roles", roles);

      // 3) 액세스 토큰 재발급 (회전하지 않음: rotateRefresh=false)
      Map<String, String> result = jwtTokenProvider.refresh(refreshToken, newAccessClaims, false);
      String newAccess = result.get("accessToken");

      // 4) 응답 바디(JwtDto): 로그인 시와 동일 구조 유지
      if (userDetails instanceof DiscodeitUserDetails d) {
        return new JwtDto(d.getUserDto(), newAccess);
      }
      return new JwtDto(null, newAccess);

    } catch (RuntimeException e) {
      // JwtTokenProvider 내부에서 발생한 검증/만료 예외를 401로 매핑
      throw new InvalidRefreshTokenException(e.getMessage());
    }
  }
}
