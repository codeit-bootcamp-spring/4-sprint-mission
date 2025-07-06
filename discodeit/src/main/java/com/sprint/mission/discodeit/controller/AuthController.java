package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.LoginResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class AuthController {
    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST,
                    consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto login = authService.login(loginRequestDto);

        return ResponseEntity.ok(login);
    }
}
