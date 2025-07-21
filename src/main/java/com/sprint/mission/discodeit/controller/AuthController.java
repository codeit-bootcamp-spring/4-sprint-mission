package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 (AUth)", description = "사용자 로그인과 관련된 API입니다.")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "기존 사용자로 로그인합니다.")
  @PostMapping("/login")
  public ResponseEntity<User> login(
      @RequestBody LoginRequest loginRequest) {
    User user = authService.login(loginRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
