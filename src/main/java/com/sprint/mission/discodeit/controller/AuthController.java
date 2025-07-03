package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/discodeit/login")
public class AuthController {
    private final AuthService authService;
    private final UserStatusService userStatusService;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> loginUser(@ModelAttribute LoginRequestDto loginRequestDto,
                                                         HttpSession session) {
        UserResponseDto logInUser = authService.logInUser(loginRequestDto);
        userStatusService.updateUserStatus(logInUser.getUserId());
        session.setAttribute("loginUser", logInUser);

        return ResponseEntity.ok(logInUser); // 201 Created
    }

    @GetMapping("/myPage")
    public ResponseEntity<?> myPage(HttpSession session) {
        UserResponseDto loginUser = (UserResponseDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        return ResponseEntity.ok("환영합니다, " + loginUser.getUserName() + "님!");
    }
}
