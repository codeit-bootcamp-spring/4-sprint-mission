package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserStatusService userStatusService;
    private final BinaryContentMapper binaryContentMapper;

    @Operation(summary = "User 등록", operationId = "create", responses = {
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = UserCreateRequest.class))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = @ExampleObject(value = "User with email already exists")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                 @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile) {
        BinaryContentPostDto binaryContentPostDto = null;
        if (profile != null) {
            binaryContentPostDto = binaryContentMapper.ofBinaryContentPostDto(profile);
        }
        UserCreateResponseDto response = userService.createUser(userCreateRequest, binaryContentPostDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User 정보 수정", operationId = "update", responses = {
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id not found"))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = @ExampleObject("user with email already exists"))),
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity update(@Parameter(description = "수정할 User ID")
                                 @PathVariable("user-id") UUID userId,
                                 @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                 @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile) {
        BinaryContentPostDto binaryContentPostDto = null;
        if (profile != null) {
            binaryContentPostDto = binaryContentMapper.ofBinaryContentPostDto(profile);
        }
        UserResponseDto response = userService.updateUser(userId, userUpdateRequest, binaryContentPostDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User 삭제", operationId = "delete", responses = {
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{user-id}")
    public ResponseEntity delete(@Parameter(description = "삭제할 User ID")
                                 @PathVariable("user-id") UUID userId) {
        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "전체 User 목록 조회", operationId = "findAll", responses = {
            @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> userDtos = userService.findAll();
        return ResponseEntity.ok(userDtos);
    }


    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId", responses = {
            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "UserStatus with userId not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation"))),
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트 됨", content = @Content(schema = @Schema(implementation = UserStatusResponseDto.class)))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{user-id}/userStatus")
    public ResponseEntity findUserStatus(@Parameter(description = "상태를 변경할 User ID")
                                         @PathVariable("user-id") UUID userId) {
        UserStatusResponseDto response = userStatusService.updateByUserId(userId);

        return ResponseEntity.ok(response);
    }

}
