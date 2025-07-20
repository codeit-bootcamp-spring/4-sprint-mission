package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthDto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto.*;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
@Tag(name = "Auth", description = "로그인 API")
public class AuthController {
    private final AuthService authService;

    //사용자 로그인
    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "{\"message\": \"Wrong password\"}"))
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "{\"message\": \"User with username {username} not found\"}")
                    )
            )
    })
    @PostMapping(value = "/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequest request) {
        UserResponseDto userLogin = authService.login(request);
        return ResponseEntity.ok(userLogin);
    }
}
