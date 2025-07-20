package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/*
 * 기존: /v1/users, PathVariable: user-id
 * 요구사항: /api/users, PathVariable: userId
 * 요구사항에 맞춰 변경하였습니다.
 *
 * CommonResponse를 만들고 스웨거 문서에 반영도 했습니다.
 * 하지만 코드잇에서 주어진 API 스펙과 프론트엔드 코드와 맞지 않아 모두 제거했습니다.
 * 그래서 CommonResponse가 쓰인 곳은 없지만, CommonResponse를 비롯한 에러를 담은 응답 코드에 대한 피드백을 받으면 좋을 것 같아서 함께 올립니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> create(
            @RequestPart("userCreateRequest") @Valid UserCreateDto dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        UserResponseDto created = userService.create(dto, profile);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping(
            path = "/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponseDto> updateUser
            (@PathVariable("userId") UUID userId,
             @RequestPart("userUpdateRequest") UserUpdateDto dto,
             @RequestPart(value = "profile", required = false) MultipartFile profile
            ) {
        return ResponseEntity.ok(userService.update(userId, dto, profile));
    }


    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserResponseDto> updateUserStatusByUserId(@PathVariable("userId") UUID userId) {
        userStatusService.updateByUserId(userId);
        UserResponseDto response = userService.find(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
