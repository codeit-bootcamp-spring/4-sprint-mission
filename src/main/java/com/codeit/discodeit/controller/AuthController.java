package com.codeit.discodeit.controller;


import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> loginUser(@RequestBody LoginRequestDto loginRequestDto) {

    User loginUser = authService.logInUser(loginRequestDto);

    // 필요하면 비밀번호 제거 또는 DTO 변환
    return ResponseEntity.ok(loginUser);
  }
}