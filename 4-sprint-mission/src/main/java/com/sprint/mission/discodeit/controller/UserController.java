package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> createUser(@ModelAttribute UserMultipartDto dto) {
        return ResponseEntity.ok(
                userService.createUser(userMapper.UserMultipartDtoToUserCreateDto(dto)));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<UserResponseDto> updateUser(@ModelAttribute UserMultipartUpdateDto dto) {
        return ResponseEntity.ok(
                userService.updateUser(userMapper.UserMultipartUpdateDtoToUserUpdateDto(dto)));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@RequestParam UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        List<UserResponseDto> userResponseDtos = userService.findAllUser();
        return ResponseEntity.ok(userResponseDtos);
    }

    @RequestMapping(value = "/status", method = RequestMethod.PUT)
    public ResponseEntity<UserStatusResponseDto> updateUserStatus(
            @RequestBody UserStatusUpdateDto userStatusUpdateDto) {
        UserStatusResponseDto userStatusResponseDto =
                userStatusService.updateUserStatusByUserId(userStatusUpdateDto.id());
        return ResponseEntity.ok(userStatusResponseDto);
    }
}
