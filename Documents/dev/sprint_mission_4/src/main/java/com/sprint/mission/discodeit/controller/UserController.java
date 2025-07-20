package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    //의존 관계 설정
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserStatusService userStatusService;
    private final UserStatusMapper userStatusMapper;


    public UserController(UserService userService, UserMapper userMapper,
                          UserStatusService userStatusService, UserStatusMapper userStatusMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userStatusService = userStatusService;
        this.userStatusMapper = userStatusMapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> createUser(@ModelAttribute UserCreateDto dto) {
        UserResponseDto user = userService.create(dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("user-id") UUID userId, @RequestBody UserUpdateDto dto) {
        UserResponseDto user = userService.update(dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public ResponseEntity deleteUser(@PathVariable("user-id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findAll")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{user-id}/status")
        public ResponseEntity<UserStatusResponseDto> updateUserStatus (@PathVariable("user-id") UUID userId, @RequestBody UserStatusUpdateDto dto) {
        UserStatusResponseDto response = userStatusService.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

