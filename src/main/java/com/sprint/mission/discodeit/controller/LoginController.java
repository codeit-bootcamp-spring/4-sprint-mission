package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.LoginDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    // 로그인 서비스가 필요해보인다....
    private final LoginService loginService;
    // 사용자는 로그인 할 수 있다. 새롭게 로그인 한거니까 POST
    @PostMapping
    public ResponseEntity<LoginDto> login(@RequestBody LoginRequest loginRequest) {
        LoginDto nowUserLogin = loginService.login(loginRequest);
        return ResponseEntity.ok().body(nowUserLogin); // body인가 그냥 ok인가
    }
}
