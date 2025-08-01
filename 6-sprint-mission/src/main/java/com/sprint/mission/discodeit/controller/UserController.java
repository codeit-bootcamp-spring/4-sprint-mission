package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAllUser();
        return ResponseEntity.ok(users);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserDto> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserCreateServiceRequest serviceRequest =
                new UserCreateServiceRequest(
                        userCreateRequest.username(),
                        userCreateRequest.email(),
                        userCreateRequest.password(),
                        profile);

        UserDto createdUser = userService.createUser(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserUpdateServiceRequest serviceRequest =
                new UserUpdateServiceRequest(
                        userId,
                        userUpdateRequest.newUsername(),
                        userUpdateRequest.newEmail(),
                        userUpdateRequest.newPassword(),
                        profile);

        UserDto updatedUser = userService.updateUser(userId, serviceRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateUserStatus(
            @PathVariable UUID userId, @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatusUpdateDto dto =
                new UserStatusUpdateDto(userId, userStatusUpdateRequest.newLastActiveAt());

        UserStatusDto updatedStatus = userStatusService.updateUserStatusByUserId(userId, dto);
        return ResponseEntity.ok(updatedStatus);
    }
}
