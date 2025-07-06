package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.form.UserForm;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.OptionalBinaryContentMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final OptionalBinaryContentMapper binaryContentMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> postUser(@ModelAttribute UserForm userCreateForm) {

        Optional<BinaryContentCreateRequest> optionalProfile = binaryContentMapper.toBinaryContentCreateRequest(userCreateForm);
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                userCreateForm.username(),
                userCreateForm.email(),
                userCreateForm.password()
        );
        User user = userService.create(userCreateRequest, optionalProfile);
        UserDto userDto = userService.makeDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> patchUser(@PathVariable("user-id") UUID userId, @ModelAttribute UserForm userForm) {

        Optional<BinaryContentCreateRequest> optionalProfile = binaryContentMapper.toBinaryContentCreateRequest(userForm);
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                userForm.username(),
                userForm.email(),
                userForm.password()
        );

        User editedUser = userService.update(userId, userUpdateRequest, optionalProfile);
        UserDto userDto = userService.makeDto(editedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);

    }

    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = userService.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{user-id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable("user-id") UUID userId, @RequestBody UserStatusUpdateRequest request) {
        userStatusService.updateByUserId(userId, request);
        UserDto userDto = userService.find(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
