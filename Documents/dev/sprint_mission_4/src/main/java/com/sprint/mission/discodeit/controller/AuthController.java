package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authservice.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> login(@RequestBody AuthLoginRequestDto dto) {
    UserResponseDto login = authService.login(dto);
    return ResponseEntity.ok(login);
    }
}
