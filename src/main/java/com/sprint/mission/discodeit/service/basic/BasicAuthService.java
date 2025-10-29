package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.constant.AuthErrorCode;
import com.sprint.mission.discodeit.constant.TokenErrorCode;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.TokenException;
import com.sprint.mission.discodeit.exception.UserAuthException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtProvider;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

    @Value("${admin.username}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    private final JwtProvider jwtProvider;

    private final JwtRegistry jwtRegistry;

    private final UserMapper userMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateRole(RoleUpdateRequest roleUpdateRequest) {
        User user = userRepository.findById(roleUpdateRequest.getUserId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Role oldRole = user.getRole();
        Role newRole = roleUpdateRequest.getNewRole();
        user.updateRole(newRole);

        jwtRegistry.invalidateJwtInformationByUserId(user.getId());
        userRepository.save(user);

        RoleUpdatedEvent roleUpdateEvent = new RoleUpdatedEvent(user.getId(), oldRole, newRole);
        applicationEventPublisher.publishEvent(roleUpdateEvent);
        return userMapper.toResponse(user);

    }

    @Transactional
    public void registerAdmin() {
        Optional<User> existAdmin = userRepository.findByEmail(adminEmail);

        if(existAdmin.isPresent()) {
            return;
        }

        User newAdmin = User.builder()
                .username(adminName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(newAdmin);
    }

    @Override
    public JwtInformation refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            log.error("Refresh token is missing or blank");
            throw new TokenException(TokenErrorCode.INVALID_REFRESH_TOKEN_FORMAT);
        }
        if (!jwtProvider.validateToken(refreshToken)) {
            log.error("Invalid refresh token: {}", refreshToken);
            throw new TokenException(TokenErrorCode.INVALID_REFRESH_TOKEN);
        }
        if(!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.error("Expired refresh token: {}", refreshToken);
            throw new TokenException(TokenErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
            throw new UserAuthException(AuthErrorCode.INVALID_USER);
        }

        try {
            String newAccessToken = jwtProvider.createAccessToken(discodeitUserDetails);
            String newRefreshToken = jwtProvider.createRefreshToken(discodeitUserDetails);
            log.debug("Access token refreshed for user: {}", username);

            JwtInformation newJwtInformation = new JwtInformation(
                    discodeitUserDetails.getUserResponse(),
                    newAccessToken,
                    newRefreshToken
            );
            jwtRegistry.rotateJwtInformation(
                    refreshToken,
                    newJwtInformation
            );

            return newJwtInformation;

        } catch (JOSEException e) {
            log.error("Failed to generate new tokens for user: {}", username, e);
            throw new TokenException(TokenErrorCode.TOKEN_CREATION_ERROR);
        }
    }

}
