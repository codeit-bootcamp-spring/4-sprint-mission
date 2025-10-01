package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Auth", description = "인증 API")
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final UserMapper userMapper;

    @GetMapping("/csrf-token")
    public ResponseEntity getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청 : {}", tokenValue);
        return ResponseEntity.status(203).build();
    }

    @GetMapping("/me")
    public ResponseEntity getUserDto(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.debug("현재 사용자 정보 조회 id = {}", userDetails.getUserDto().id());
        UserDto userDto = userDetails.getUserDto();
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/role")
    public ResponseEntity updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest) {
        log.info("사용자 권한 수정 요청 userId = {}", roleUpdateRequest.userId());
        User user = authService.updateRole(roleUpdateRequest);
        UserDto response = userMapper.toDto(user);
        log.info("사용자 권한 수정 응답 userId = {}", user.getId());
        return ResponseEntity.ok(response);
    }
}
