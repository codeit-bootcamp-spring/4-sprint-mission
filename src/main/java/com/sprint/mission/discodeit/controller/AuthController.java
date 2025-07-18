package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/login")
@Tag(name = "Auth",description = "")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "로그인", description = "유저 정보를 사용하여 로그인")
    @ApiResponse(responseCode = "201", description = "로그인에 성공하였습니다")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> login(@RequestBody  LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        UserDto userDto = userService.makeDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
