package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user_service_dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.user_service_dto.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UserWithStatusResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/discodeit/user")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> createUser(@ModelAttribute UserCreateRequestDto userCreateRequestDto, Model model) throws IOException {
        UserResponseDto createdUser = userService.createUser(userCreateRequestDto);
        userStatusService.createUserStatus(createdUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userName") String userName) {
        UserResponseDto user = userService.findUserDtoByUserName(userName); // 존재하지 않으면 예외 발생
        return ResponseEntity.ok(user); // 200 OK
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable("userName") String userName) {
        UserResponseDto user = userService.findUserDtoByUserName(userName); // 존재하지 않으면 예외 발생
        userService.deleteUser(user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserWithStatusResponseDto>> getUsers() {
        List<UserWithStatusResponseDto> userList = userService.findAllUserAndUserStatus();
        return ResponseEntity.ok(userList);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAllUser() {
        List<UserDto> userDtoList = userService.findAllUserDto();
        return ResponseEntity.ok(userDtoList);

    }
        /*public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        Boolean online) */

}
