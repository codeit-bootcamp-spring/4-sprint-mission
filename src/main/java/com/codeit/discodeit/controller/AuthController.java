package com.codeit.discodeit.controller;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.service.AuthService;
import com.codeit.discodeit.swagger.SwaggerAuthController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/auth")
public class AuthController implements SwaggerAuthController {

  private final AuthService authService;

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

    UserDto loginUserDto = authService.logInUser(loginRequestDto);
    return ResponseEntity.ok(loginUserDto);
  }
}