package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto.*;
import com.sprint.mission.discodeit.dto.UserStatusDto.*;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User 관련 API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // ============================== POST - 유저 등록 ==============================
  @Operation(summary = "User 등록", operationId = "create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = UserGetDto.class))
      ),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "User with email {email} already exists"))
      )
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserGetDto> createUser(
      @Parameter(description = "User 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,// 심화
//      @ModelAttribute UserCreateRequest userCreateRequest,
      @Parameter(description = "User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserGetDto user = userService.create(userCreateRequest, profile);

    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }


  // ============================== PATCH - 유저 수정 ==============================
  @Operation(summary = "User 정보 수정", operationId = "update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = UserUpdateResponse.class))
      ),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "User with id {userId} not found"))
      ),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "user with email {newEmail} already exists"))
      )
  })
  @PatchMapping(path = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserUpdateResponse> updateUser(
      @Parameter(description = "수정할 User ID")
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest, // 심화
//      @ModelAttribute UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserUpdateResponse userUpdateResponse = userService.update(userId, userUpdateRequest, profile);
    return ResponseEntity.ok(userUpdateResponse);
  }


  // ============================== DELETE - 유저 삭제 ==============================
  @Operation(summary = "User 삭제", operationId = "delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "User with id {id} not found"))
      )
  })
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteMember(
      @Parameter(description = "삭제할 User ID") @PathVariable(value = "userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }


  // ============================== PATCH - 유저 상태 업데이트 ==============================
  @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(mediaType = "*/*",
              schema = @Schema(implementation = UserStatusUpdateResponse.class))
      ),
      @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(mediaType = "*/*",
              examples = @ExampleObject(value = "UserStatus with userId {userId} not found"))
      )
  })
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusUpdateResponse> updateUserStatus(
      @Parameter(description = "상태를 변경할 User ID")
      @PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatusUpdateResponse userStatusResponse = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(userStatusResponse);
  }


  // ============================== GET - 전체 유저 조회 ==============================
  @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
      content = @Content(mediaType = "*/*",
          array = @ArraySchema(schema = @Schema(implementation = AllUserGetDto.class)))
  )
  @GetMapping
  public ResponseEntity<List<AllUserGetDto>> getUsers() {
    List<AllUserGetDto> userGetDtos = userService.findAll();
    return ResponseEntity.ok(userGetDtos);
  }
}
