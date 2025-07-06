package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sprint.mission.discodeit.dto.UserDto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserUpdateDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    //사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserResponseDto> createUser(@ModelAttribute UserCreateDto createDto) {

        UserCreateDto userDto = new UserCreateDto(
                createDto.getUsername(),
                createDto.getEmail(),
                createDto.getPassword(),
                null,
                createDto.getProfile()
        );

        BinaryContentCreateDto binaryDto = new BinaryContentCreateDto(
                null,
                null,
                null,
                createDto.getProfile()
        );

        UserResponseDto response = userService.create(userDto, binaryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //사용자 전체 조회
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<UserResponseDto>> findAllUsers() {
        List<UserResponseDto> all = userService.findAll();
        return ResponseEntity.ok(all);
    }

    //사용자 수정
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("user-id") UUID userId, @ModelAttribute UserUpdateDto updateDto) {

        UserUpdateDto userUpdateDto = new UserUpdateDto(
                userId,
                updateDto.getUsername(),
                updateDto.getEmail(),
                updateDto.getPassword(),
                updateDto.getNewProfile()
        );

        UserResponseDto responseDto = userService.update(userUpdateDto);
        return ResponseEntity.ok(responseDto);
    }

    //사용자 삭제
    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteUser(@PathVariable("user-id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    //사용자의 온라인 상태 업데이트
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PATCH,
                    consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<UserStatusResponseDto> updateUserStatus(@PathVariable("user-id") UUID userId, @RequestBody UserStatusUpdateDto updateDto) {

        UserStatusResponseDto responseDto = userStatusService.updateByUserId(userId);
        return ResponseEntity.ok(responseDto);
    }
}
