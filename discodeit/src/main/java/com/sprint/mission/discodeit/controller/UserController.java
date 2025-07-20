package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sprint.mission.discodeit.dto.UserDto.*;
import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;


    //사용자 등록
    @Operation(summary = "User 등록", operationId = "create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
            content = {@Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = UserResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "User with email {email} already exists")
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> create(
                                                @Parameter(description = "User 생성 정보",
                                                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                                @Parameter(description = "User 프로필 이미지 (선택)")
                                                @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserResponseDto user = userService.create(userCreateRequest, profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    //사용자 전체 조회
    @Operation(summary = "User 전체 조회", description = "등록된 모든 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 User 목록이 성공적으로 조회됨",
                    content = @Content(
                            mediaType = "*/*",
                            array = @ArraySchema(schema = @Schema(implementation = AllUserResponseDto.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<AllUserResponseDto>> findAll() {
        List<AllUserResponseDto> all = userService.findAll();
        return ResponseEntity.ok(all);
    }

    /*//특정 사용자 조회
    @Operation(summary = "User 조회", description = "특정 사용자 ID로 사용자를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 사용자가 성공적으로 조회됨",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class),
                    examples = {@ExampleObject("""
                            {
                              "id": "dd6e2959-3ef1-49a6-ba58-d310a71d3e36",
                              "username": "viichan",
                              "email": "viichan@nate.com",
                              "profileId": "8aacea1d-bd7e-4117-b19c-5ffd693787c6",
                              "userStatusDto": {
                                "status": "offline",
                                "lastActiveAt": "2025-07-08T08:48:31.726974Z"
                              }
                            }
                            """)
            })}),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User with id {id} not found\"}")
                    )
            )
    })
    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<UserDto> findUserById(@PathVariable("userId") UUID userId) {
        UserDto user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }*/

    //사용자 수정
    @Operation(summary = "User 정보 수정", operationId = "update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자가 성공적으로 수정됨",
                    content = @Content(mediaType = "*/*",
                            schema = @Schema(implementation = UserUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "user with email {newEmail} already exists"))),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
                    content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User with id {userId} not found")))
    })
    @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<UserUpdateResponse> update(@Parameter(description = "수정할 User ID")
                                                      @PathVariable("userId") UUID userId,
                                                  @Parameter(description = "수정할 사용자 정보")
                                                      @RequestPart("userUpdateRequest") UserUpdateRequest updateDto,
                                                  @Parameter(description = "수정할 프로필 이미지")
                                                      @RequestPart(value = "profile", required = false) MultipartFile profile) {

        UserUpdateResponse updatedUser = userService.update(userId, updateDto, profile);
        return ResponseEntity.ok(updatedUser);
    }

    //사용자 삭제

    @Operation(summary = "User 삭제", description = "사용자를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "사용자가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "{\"message\": \"User with id {userId} not found\"}")
                    )
            )
    })
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity delete(@Parameter(description = "삭제할 User ID")
                                 @PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    //사용자의 온라인 상태 업데이트
    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserStatusUpdateResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "UserStatus with userId {userId} not found"))
            )
    })
    @PatchMapping(value = "/{userId}/userStatus")
    public ResponseEntity<UserStatusUpdateResponse> updateUserStatusByUserId(
                                                                            @Parameter(description = "상태 변경 User ID")
                                                                            @PathVariable("userId") UUID userId,
                                                                            @RequestBody UserStatusUpdateRequest updateRequest) {

        UserStatusUpdateResponse updatedStatus = userStatusService.update(userId, updateRequest);
        return ResponseEntity.ok(updatedStatus);
    }
}
