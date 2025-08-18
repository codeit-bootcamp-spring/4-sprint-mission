package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
                                 @RequestPart(value = "profile", required = false) MultipartFile profile) {
        log.info("POST /api/users 호출");
        UUID binaryContentId = Optional.ofNullable(profile)
                .map(file -> {
                    BinaryContent binaryContent = binaryContentService.create(profile);
                    return binaryContent.getId();
                })
                .orElse(null);
        User user = userService.createUser(userCreateRequest, binaryContentId);
        userStatusService.create(user);
        UserDto response = userMapper.toDto(user);
        log.debug("생성 응답 = {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity update(@PathVariable("user-id") UUID userId,
                                 @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                 @RequestPart(value = "profile", required = false) MultipartFile profile) {
        log.info("PATCH /api/users/{user-id} 호출 id = {}", userId);
        UUID binaryContentId = Optional.ofNullable(profile)
                .map(file -> {
                    BinaryContent binaryContent = binaryContentService.create(profile);
                    return binaryContent.getId();
                })
                .orElse(null);
        User user = userService.updateUser(userId, userUpdateRequest, binaryContentId);
        UserDto response = userMapper.toDto(user);

        log.debug("수정 응답 = {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public ResponseEntity delete(@PathVariable("user-id") UUID userId) {
        log.info("DELETE /api/users/{user-id} 호출 id = {}", userId);
        userService.deleteUser(userId);
        log.debug("삭제 응답 status = {}", HttpStatus.NO_CONTENT);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> response = users.stream().map(userMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}/userStatus")
    public ResponseEntity updateUserStatus(@PathVariable("user-id") UUID userId,
                                           @RequestBody UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        UserStatusDto response = userStatusMapper.toDto(userStatus);

        return ResponseEntity.ok(response);
    }
}
