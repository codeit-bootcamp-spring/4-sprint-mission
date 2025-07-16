package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.login.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.WrongPasswordException;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class BasicAuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    try {
      UserDto userDto = authService.login(loginRequest);
      return ResponseEntity.ok(userDto);
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    } catch (WrongPasswordException e) {
      return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
  }

  public static record ErrorResponse(String message) {

  }
}
