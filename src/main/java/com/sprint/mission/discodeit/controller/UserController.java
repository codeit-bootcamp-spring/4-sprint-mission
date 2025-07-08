package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UpdateUserDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    // 컨트롤러는 서비스를 받아서 처리한다
   private final UserService userService;
   private final UserStatusService userStatusService;
    // 사용자를 등록할 수 있다 - POST
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        userService.createUser(userDto);

        return ResponseEntity.ok().body(userDto);
    }
    // 사용자 정보를 수정할 수 있다 - PATCH
    @PatchMapping("/{user-id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("user-id") UUID userId,
                                              @RequestBody UpdateUserDto updateUserDTO) {
        UserDto updateUser = userService.updateUser(userId, updateUserDTO);
        return ResponseEntity.ok(updateUser);
    }
    // 사용자 정보를 삭제할 수 있다 - DELETE
    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    // 모든 사용자를 조회할 수 있다 - GET, 모든 사용자를 조회할땐 인자가 필요없으니 mapping 설정을 안해도 될듯..
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> findAllUser = userService.findAll();
        return ResponseEntity.ok(findAllUser);
    }
    // 사용자의 온라인 상태를 업데이트 할 수 있다 - 업데이트니까 PATCH
    @PatchMapping("/{user-id}")
    public ResponseEntity<UserDto> updateStatus(@PathVariable("user-id") UUID userId,
                                       @RequestBody UserStatusRequest userStatusRequest) {
         UserDto userStatusUpdate = userStatusService.updateUserStatus(userId, userStatusRequest);

        return ResponseEntity.ok(userStatusUpdate);
    }

}
