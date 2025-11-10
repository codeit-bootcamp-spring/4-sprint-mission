package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.registry.JwtInformation;
import com.sprint.mission.discodeit.auth.registry.JwtRegistry;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.JwtDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.TokenResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Auth", description = "인증 API")
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final JwtRegistry jwtRegistry;

    @GetMapping("/csrf-token")
    public ResponseEntity getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청 : {}", tokenValue);
        return ResponseEntity.status(203).build();
    }

    @PutMapping("/role")
    public ResponseEntity updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest) {
        log.info("사용자 권한 수정 요청 userId = {}", roleUpdateRequest.userId());
        User user = authService.updateRole(roleUpdateRequest);
        UserDto response = userMapper.toDto(user);
        log.info("사용자 권한 수정 응답 userId = {}", user.getId());
        jwtRegistry.invalidateJwtInformationByUserId(response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity reissueAccessToken(@CookieValue("REFRESH_TOKEN") String refreshToken, HttpServletResponse response) {
        log.info("AccessToken 재발급 요청");
        Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
        User findUser = userService.findByEmail((String) claims.get("sub"));
        UserDto userDto = userMapper.toDto(findUser);

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.warn("RefreshToken이 만료 됨 refreshToken = {}", refreshToken);
            throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN, Map.of("refreshToken", refreshToken));
        }
        TokenResponse tokenResponse = authService.reissueToken(claims);

        JwtDto responseDto = new JwtDto(userDto, tokenResponse.accessToken());

        Cookie newRefreshToken = new Cookie("REFRESH_TOKEN", tokenResponse.refreshToken());
        newRefreshToken.setMaxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);
        newRefreshToken.setPath("/");
        response.addCookie(newRefreshToken);

        JwtInformation jwtInformation = new JwtInformation(userDto, responseDto.accessToken(), tokenResponse.refreshToken());
        jwtRegistry.rotateJwtInformation(refreshToken, jwtInformation);
        log.debug("AccessToken 재발급 성공 및 Rotate 응답 완료");
        return ResponseEntity.ok(responseDto);
    }
}
