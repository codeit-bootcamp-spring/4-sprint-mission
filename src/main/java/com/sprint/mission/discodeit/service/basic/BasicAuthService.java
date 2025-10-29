package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.TokenPair;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionManager sessionManager;
    //
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
    public TokenPair refreshTokens(String oldRefreshToken) {
        log.debug("토큰 재발급 시작: oldRefreshToken:{}", oldRefreshToken);
        try {
            Map<String, Object> claims = jwtTokenProvider.getClaims(oldRefreshToken);
            String username = (String) claims.get("sub");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
                throw new DiscodeitException(ErrorCode.INVALID_USER_DETAILS);
            }
            UserDto userDto = ((DiscodeitUserDetails) userDetails).getUserDto();
            String roleName = userDto.role().name();

            String newAccessToken = jwtTokenProvider.refreshAccessToken(oldRefreshToken, roleName);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

            JwtDto jwtDto = new JwtDto(userDto, newAccessToken);
            return new TokenPair(jwtDto, newRefreshToken);

        } catch (Exception e) {
            log.error("토큰 재발급 실패: error={}", e.getMessage(), e);
            throw new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
