package com.codeit.discodeit.controller;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.dto.user_status_dto.UserStatusUpdateRequest;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import com.codeit.discodeit.swagger.SwaggerUserController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@RequestMapping("/api/users")
public class UserController implements SwaggerUserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> createUser(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {

    log.info("[POST /api/users] 요청 수신 - userCreateRequest={}, profileFileName={}, profileSize={}",
        userCreateRequest,
        profile != null ? profile.getOriginalFilename() : "없음",
        profile != null ? profile.getSize() : 0);

    userCreateRequest.setProfileImage(profile);
    UserDto createdUser = userService.createUser(userCreateRequest);

    log.info("[POST /api/users] 생성 완료 - createdUserId={}", createdUser.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAllUser();
    return ResponseEntity.ok(users);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    log.info("[DELETE /api/users/{}] 요청 수신", userId);
    userService.findUserDtoByUserId(userId);
    userService.deleteUser(userId);
    log.info("[DELETE /api/users/{}] 삭제 완료", userId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage) throws IOException {

    log.info("[PATCH /api/users/{}] 요청 수신 - userUpdateRequest={}, profileFileName={}, profileSize={}",
        userId,
        userUpdateRequest,
        profileImage != null ? profileImage.getOriginalFilename() : "없음",
        profileImage != null ? profileImage.getSize() : 0);

    userUpdateRequest.setUserId(userId);
    BinaryContent profileImg = BinaryContentMapper.attachmentToBinaryContent(profileImage);
    UserDto updatedUser = userService.updateUser(userUpdateRequest, profileImg,
        profileImage != null ? profileImage.getBytes() : null);

    log.info("[PATCH /api/users/{}] 수정 완료", userId);
    return ResponseEntity.ok(updatedUser);
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest request) {
    UserStatusDto updatedStatus = userStatusService.updateUserStatus(userId, request.getNewLastActiveAt());
    return ResponseEntity.ok(updatedStatus);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
    UserDto user = userService.findUserDtoByUserId(userId);
    return ResponseEntity.ok(user);
  }
}
