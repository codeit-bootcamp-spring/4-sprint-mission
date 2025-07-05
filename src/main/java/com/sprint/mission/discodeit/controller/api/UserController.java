package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(@ModelAttribute @Valid UserDto.create request) {
        User createdUser = userService.create(request);
        UserDto.response response = createdUser.toDto(isOnlineByUserId(createdUser.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/{user-id}")
    public ResponseEntity<?> updateUser(@PathVariable("user-id") UUID id,
                                        @ModelAttribute @Valid UserDto.update userUpdateFormRequest) {
        User updatedUser = userService.update(id, userUpdateFormRequest);
        UserDto.response response = updatedUser.toDto(isOnlineByUserId(updatedUser.getId()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user-id") UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findAll")
    public ResponseEntity<List<UserDto.response>> getAllUsers() {
        List<UserDto.response> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{user-id}")
    public ResponseEntity<?> updateUserStatus(@PathVariable("user-id") UUID id) {
        UserStatusDto.update userStatusDto = new UserStatusDto.update(Instant.now());
        UserStatus userStatus = userStatusService.updateByUserId(id, userStatusDto);
        UserStatusDto.response response = userStatus.toDto(isOnlineByUserId(id));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto request) {
        // login
        User loginUser = authService.login(request);

        // userStatus => online
        Instant loginTime = Instant.now();
        UserStatusDto.update userStatusDto = new UserStatusDto.update(loginTime);
        userStatusService.updateByUserId(loginUser.getId(), userStatusDto);

        UserDto.response response = loginUser.toDto(isOnlineByUserId(loginUser.getId()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    private Boolean isOnlineByUserId(UUID userId) {
        return userStatusService.findByUserId(userId).isOnline();
    }
}
