package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auths")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;


    // 권한 관리
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
