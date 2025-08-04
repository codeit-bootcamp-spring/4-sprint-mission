package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
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
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // [ ] 사용자를 등록할 수 있다.
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity registerUser(@ModelAttribute UserCreateRequest request,@ModelAttribute BinaryContentCreateRequest binaryContentCreateRequest) {

        UserResponseDto userResponseDto = userService.create(request, Optional.ofNullable(binaryContentCreateRequest));

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    // [ ] 사용자 정보를 수정할 수 있다.
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@PathVariable("user-id") String userId, @ModelAttribute UserUpdateRequest userUpdateRequest,@ModelAttribute BinaryContentCreateRequest profileCreateRequest) {

        UserResponseDto responseDto = userService.update(UUID.fromString(userId), userUpdateRequest, Optional.ofNullable(profileCreateRequest));

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // [ ] 사용자를 삭제할 수 있다.
    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@PathVariable("user-id") String userId) {

        userService.delete(UUID.fromString(userId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // [ ] 모든 사용자를 조회할 수 있다.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllUsers() {

         List<UserResponseDto> userResponseDtos = userService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDtos);
    }

    // [ ] 사용자의 온라인 상태를 업데이트할 수 있다.
    @RequestMapping(value = "/{user-id}/status", method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatus(@PathVariable("user-id") String userId, @RequestBody UserStatusUpdateRequest request) {

        UserStatusResponseDto userStatusResponseDto = userStatusService.updateByUserId(UUID.fromString(userId), request);

        return ResponseEntity.status(HttpStatus.OK).body(userStatusResponseDto);
    }
}