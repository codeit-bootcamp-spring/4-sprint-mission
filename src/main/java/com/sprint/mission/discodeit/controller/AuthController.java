package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.TokenReissueRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.response.TokenResponse;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.basic.TokenService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
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
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final TokenService tokenService;


  // where client first calls to get the initial CSRF token
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrToken(CsrfToken csrfToken) {
    // this method creates token in CookieCsrfTokenRepository
    // when the response is sent back, the HTTP response header includes Set-Cookie + created token
    // then client saves that cookie (that includes the token)
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity
        .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }
  // In future requests, the token is sent back to the server in 2 places simultaneously
  // - Cookie (Automatic): The browser automatically includes the XSRF-TOKEN cookie with every request because it's stored for that domain
  // - Header (Manual): Frontend JS code reads the value from the XSRF-TOKEN cookie and *manually* adds it to a request header named X-XSRF-TOKEN
  //   - possible coz cookie was set with HttpOnly=false
  // The server simply checks if the token in cookie matches the token in the header

  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(
      @RequestBody RoleUpdateRequest userRoleUpdateRequest
  ) {
    UserDto userDto = authService.updateRole(userRoleUpdateRequest);
    return ResponseEntity.ok(userDto);
  }


  /**
   * Refresh Token을 이용한 Access Token 재발급 API
   */
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshTokenValue,
      HttpServletResponse response
  ) {

    if (refreshTokenValue == null) {
      // Tell the frontend "Unauthorized" so it can show the login page.
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Refresh token is missing.");
    }

    TokenResponse tokenResponse = tokenService.reissueToken(refreshTokenValue);

    //creating new cookie
    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", tokenResponse.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);
    JwtDto jwtDto = new JwtDto(tokenResponse.getUserDto(), tokenResponse.getAccessToken());
    return ResponseEntity.ok(jwtDto);
  }

}
