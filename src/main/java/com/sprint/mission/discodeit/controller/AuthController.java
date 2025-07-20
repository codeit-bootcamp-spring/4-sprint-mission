package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*
 * 기존: /v1/login
 * 요구사항: /api/auth/login
 * 요구사항에 맞춰 변경하였습니다.
 *
 * CommonResponse를 만들고 스웨거 문서에 반영도 했습니다.
 * 하지만 코드잇에서 주어진 API 스펙과 프론트엔드 코드와 맞지 않아 모두 제거했습니다.
 * 그래서 CommonResponse가 쓰인 곳은 없지만, CommonResponse를 비롯한 에러를 담은 응답 코드에 대한 피드백을 받으면 좋을 것 같아서 함께 올립니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
