package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Validated
public interface UserApi {
    @Operation(summary = "User 등록", operationId = "create", responses = {
            @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = @ExampleObject(value = "User with email already exists")))
    })
    ResponseEntity create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                          @Parameter(description = "User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile);

    @Operation(summary = "User 정보 수정", operationId = "update", responses = {
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id not found"))),
            @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함", content = @Content(examples = @ExampleObject("user with email already exists"))),
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = UserDto.class))),
    })
    ResponseEntity update(@Parameter(description = "수정할 User ID")
                          @NotNull(message = "잘못된 ID 입니다.")
                          @PathVariable("user-id") UUID userId,
                          @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
                          @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile);

    @Operation(summary = "User 삭제", operationId = "delete", responses = {
            @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity delete(@Parameter(description = "삭제할 User ID")
                          @NotNull(message = "잘못된 ID 입니다.")
                          @PathVariable("user-id") UUID userId);

    @Operation(summary = "전체 User 목록 조회", operationId = "findAll", responses = {
            @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity findAllUsers();


//    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId", responses = {
//            @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "UserStatus with userId not found"))),
//            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation"))),
//            @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트 됨", content = @Content(schema = @Schema(implementation = UserStatusDto.class)))
//    })
//    ResponseEntity updateUserStatus(@Parameter(description = "상태를 변경할 User ID")
//                                    @NotNull(message = "잘못된 ID 입니다.")
//                                    @PathVariable("user-id") UUID userId,
//                                    @RequestBody UserStatusUpdateRequest userStatusUpdateRequest);
}
