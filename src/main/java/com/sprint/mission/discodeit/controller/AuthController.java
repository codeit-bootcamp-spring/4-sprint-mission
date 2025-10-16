package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.jwt.JwtDto;
import com.sprint.mission.discodeit.jwt.JwtProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.service.RefreshTokenService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController{

  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;
  private final JwtProvider jwtProvider;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(null);//203
  }

  @PutMapping(value = "/role", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest userUpdateRequest) {
    log.debug("권한 수정 요청: {}", userUpdateRequest.newRole());
    UserDto userDto = authService.patchRole(userUpdateRequest);
    return ResponseEntity.ok(userDto);
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] list = request.getCookies();
    String oldRefreshToken = null;

    for(Cookie cookie:list) {
      if (cookie.getName().equals("REFRESH_TOKEN")) {
        oldRefreshToken = cookie.getValue();
      }
    }

    JwtDto jwtDto = refreshTokenService.validateRefreshTokenAndReissueAccessToken(oldRefreshToken, response); // 리프레쉬 토큰 재발급
    return ResponseEntity.ok(jwtDto);
  }

}
