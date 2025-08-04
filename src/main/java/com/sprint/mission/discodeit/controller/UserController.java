package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController implements UserApi {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserStatusService userStatusService;
    private final BinaryContentMapper binaryContentMapper;
    private final UserStatusMapper userStatusMapper;
    private final BinaryContentService binaryContentService;

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                 @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UUID binaryContentId = Optional.ofNullable(profile)
                .map(file -> {
                    BinaryContent binaryContent = binaryContentService.create(profile);
                    return binaryContent.getId();
                })
                .orElse(null);
        User user = userService.createUser(userCreateRequest, binaryContentId);
        userStatusService.create(user);
        UserDto response = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity update(@Parameter(description = "수정할 User ID")
                                 @PathVariable("user-id") UUID userId,
                                 @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                 @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile) {
        UUID binaryContentId = Optional.ofNullable(profile)
                .map(file -> {
                    BinaryContent binaryContent = binaryContentService.create(profile);
                    return binaryContent.getId();
                })
                .orElse(null);
        User user = userService.updateUser(userId, userUpdateRequest, binaryContentId);
        UserDto response = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public ResponseEntity delete(@Parameter(description = "삭제할 User ID")
                                 @PathVariable("user-id") UUID userId) {
        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> response = users.stream().map(userMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}/userStatus")
    public ResponseEntity updateUserStatus(@Parameter(description = "상태를 변경할 User ID")
                                           @PathVariable("user-id") UUID userId,
                                           @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        UserStatusDto response = userStatusMapper.toDto(userStatus);

        return ResponseEntity.ok(response);
    }
}
