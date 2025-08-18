package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    //의존 관계 설정
    private final UserService userService;
    private final UserStatusService userStatusService;

    public UserController(UserService userService, UserMapper userMapper,
                          UserStatusService userStatusService, UserStatusMapper userStatusMapper, SimpleControllerHandlerAdapter simpleControllerHandlerAdapter) {
        this.userService = userService;
        this.userStatusService = userStatusService;
    }


    @Operation(summary = "User 등록", description = "새로운 User를 등록합니다.(프로필 이미지 선택적 업로드)", operationId = "create")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨,",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                             "id": "uuid...",
                                             "username": "코코",
                                             "email": "koko@example.com"
                                        }
                                       """
                            )
                        )
                    ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "USER_WITH_EMAIL_EXISTS",
                                              "message": "User with email {email} already exists"
                                            }
                                            """
                            )
                    )
            )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(@RequestPart("userCreateRequest") @Valid UserCreateRequest dto,
                                              @RequestPart(value ="profile", required = false) MultipartFile profileImage) {
        log.info("유저 생성 요청 수신: username={}, email={]", dto.getUsername(), dto.getEmail());

        if (profileImage != null) {
            log.debug("프로필 이미지 업로드: profileImageName={}", profileImage.getOriginalFilename());
        }
        UserDto user = userService.create(dto);
        log.info("유저 생성 성공: userId={}, username={}", user.getId(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    @Operation(summary = "User 정보 수정", description = "User ID를 기반으로 유저 정보를 수정합니다.", operationId = "update")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": "uuid...",
                                                "username": "newKoko",
                                                "email": "newkoko@example.com"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "USER_WITH_ID_NOT_FOUND",
                                              "message": "User with id {userId} not found"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username을 사용하는 User가 이미 존재함",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "USER_WITH_EMAIL_EXISTS",
                                              "message": "user with email {newEmail} already exists"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") UUID userId,
                                              @RequestPart("userUpdateRequest") @Valid UserUpdateRequest dto,
                                              @RequestPart(value = "profile", required = false) MultipartFile profileImage) {
        log.info("유저 데이터 수정 요청 수신: userId={}", userId);

        if (profileImage != null) {
            log.debug("프로필 이미지 변경: profileImageName={}", profileImage.getOriginalFilename());
        }
        UserDto user = userService.update(dto);
        log.info("유저 데이터 수정 성공: userId={}, username={}", user.getId(), user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "User 삭제", description = "User Id를 기반으로 유저를 삭제합니다.", operationId = "delete")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제됨"
                    ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "USER_WITH_ID_NOT_FOUND",
                                              "message": "User with id {id} not found"
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") UUID userId) {
        log.info("유저 삭제 요청 수신: userId={}", userId);
        userService.delete(userId);
        log.info("유저 삭제 성공: userId={}", userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 User 목록 조회", description = "전체 User 목록을 조회합니다.", operationId = "findAll" )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    implementation = UserDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "OK",
                                              "message": "User 목록 조회 성공",
                                              "data": [
                                                {
                                                  "id": "uuid...",
                                                  "username": "코코",
                                                  "email": "koko@example.com"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "User 온라인 상태 업데이트", description = "User ID를 기반으로 User의 온라인 상태 정보를 업데이트합니다.", operationId = "updateUserStatusByUserId")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserStatusDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "userId": "uuid...",
                                                "online": true,
                                                "lastActiveAt": "2024-01-01T12:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "USERSTATUS_WITH_USERID_NOT_FOUND",
                                              "message": "UserStatus with userId {userId} not found"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{userId}/userStatus")
        public ResponseEntity<UserStatusDto> updateUserStatus (@PathVariable("userId") UUID userId, @RequestBody UserStatusUpdateDto dto) {
        UserStatusDto response = userStatusService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

