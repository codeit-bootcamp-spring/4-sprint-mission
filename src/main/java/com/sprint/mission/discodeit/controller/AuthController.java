package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginUserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/login")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid LoginUserDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
