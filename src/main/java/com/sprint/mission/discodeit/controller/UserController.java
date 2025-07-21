package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자 (User)", description = "사용자 정보 조회, 생성, 수정 및 삭제와 관련된 API입니다.")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "새로운 사용자를 등록합니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
      @Parameter(description = "사용자 생성 정보")
      UserCreateRequest userCreateRequest,

      @Parameter(description = "사용자 프로필 이미지 파일")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  @Operation(summary = "특정 사용자의 정보를 수정합니다.")
  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> update(
      @Parameter(description = "수정할 사용자의 고유 ID")
      @PathVariable("userId") UUID userId,

      @Parameter(description = "수정할 사용자 정보")
      UserUpdateRequest userUpdateRequest,

      @Parameter(description = "새로운 프로필 이미지 파일")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }
  @Operation(summary = "특정 사용자를 삭제합니다.")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 사용자의 고유 ID")
      @PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "등록된 모든 사용자 목록을 조회합니다.")
  @GetMapping()
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @Operation(summary = "등록된 특정 사용자를 조회합니다.")
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(
      @Parameter(description = "조회할 사용자의 고유 ID")
      @PathVariable("userId") UUID userId) {
    UserDto user = userService.find(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  @Operation(summary = "특정 사용자의 온라인 상태를 업데이트합니다.")
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @Parameter(description = "상태를 변경할 사용자의 고유 ID")
      @PathVariable("userId") UUID userId,

      @Parameter(description = "마지막 활동 시간 (사용자가 온라인 상태임을 나타냄")
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
