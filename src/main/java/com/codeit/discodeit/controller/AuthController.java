package com.codeit.discodeit.controller;


import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.service.AuthService;
import com.codeit.discodeit.service.UserService;
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
  private final UserService userService;
  private final UserMapper userMapper;

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) {

    User loginUser = authService.logInUser(loginRequestDto);
    UserDto userDto = userMapper.toUserDto(loginUser);
    return ResponseEntity.ok(userDto);
  }
}