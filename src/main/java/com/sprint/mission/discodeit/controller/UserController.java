package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private UserStatusService userStatusService;

    // 사용자 등록
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserDto> createUser(@ModelAttribute UserCreateRequest request) {
        System.out.println("######### postUser");
        System.out.println("# request = " + request);

        return ResponseEntity.ok(userService.create(request));
    }

    // 사용자 수정
    @RequestMapping(value = "/{user-id}", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> updateUser(@PathVariable("user-id") UUID userId,
                                              @ModelAttribute UserUpdateRequest request) {
        System.out.println("######### patchUser");
        System.out.println("# userId = " + userId);
        System.out.println("# request = " + request);

        return ResponseEntity.ok(userService.update(userId, request));
    }

    // 사용자 삭제
    @RequestMapping(value = "/{user-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        System.out.println("######### deleteUser");
        System.out.println("# userId = " + userId);

        userService.delete(userId);

        return ResponseEntity.ok().build();
    }

    // 모든 사용자 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAllUsers() {
        System.out.println("######### findAllUsers");

        return ResponseEntity.ok(userService.findAll());
    }

    // 사용자의 온라인 상태 업데이트
    @RequestMapping(value = "/{user-id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateUserStatus(@PathVariable("user-id") UUID userId,
                                                 @RequestBody UserStatusUpdateRequest request) {
        System.out.println("######### patchUserStatus");
        System.out.println("# request = " + request);


        userStatusService.updateByUserId(userId, request);

        return ResponseEntity.ok().build();
    }
}
