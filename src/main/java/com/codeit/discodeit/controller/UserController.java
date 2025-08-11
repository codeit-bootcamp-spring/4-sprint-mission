package com.codeit.discodeit.controller;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.mapper.UserStatusMapper;
import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.dto.user_status_dto.UserStatusUpdateRequest;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final UserMapper userMapper;
  private final UserStatusMapper userStatusMapper;
  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "전체 User 목록 조회", responses = {
      @ApiResponse(
          responseCode = "200",
          description = "User 목록 조회 성공",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
          )
      )
  })
  public ResponseEntity<List<UserDto>> findAll() {
    List<User> users = userService.findAllUser();
    List<UserDto> userDtoList = users.stream().map(userMapper::toUserDto)
        .toList();

    return ResponseEntity.ok(userDtoList);
  }

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "User가 성공적으로 생성됨",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = User.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = MediaType.TEXT_PLAIN_VALUE,
              examples = @ExampleObject(value = "User with email {email} already exists")
          )
      )
  })
  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<UserDto> createUser(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {

    userCreateRequest.setProfileImage(profile);
    byte[] profileBytes;
    if (profile == null || profile.isEmpty()) {
      profileBytes = BinaryContentMapper.getBasicProfileBytes(); // 기본 이미지
    } else {
      profileBytes = profile.getBytes();
    }

    User createdUser = userService.createUser(userMapper.toUser(userCreateRequest), profileBytes);
    UserDto userDto = userMapper.toUserDto(createdUser);

    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = "User with id {id} not found")
          )
      )
  })
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    userService.findUserByUserId(userId); // 존재하지 않으면 내부에서 예외 발생
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }


  @Operation(
      summary = "User 정보 수정",
      description = "multipart/form-data 형식으로 사용자 정보를 수정합니다. 사용자 정보와 프로필 이미지를 함께 보낼 수 있습니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = "User with id {userId} not found")
          )),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = "user with email {newEmail} already exists")
          ))
  })
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage)
      throws IOException {

    userUpdateRequest.setUserId(userId);

    BinaryContent profileImg = BinaryContentMapper.attachmentToBinaryContent(profileImage);
    User updatedUser = userService.updateUser(userUpdateRequest, profileImg, profileImage.getBytes());
    UserDto userDto = userMapper.toUserDto(updatedUser);
    return ResponseEntity.ok(userDto);
  }

  @Operation(
      summary = "User 온라인 상태 업데이트",
      description = "지정된 사용자 ID에 대해 온라인 상태를 변경합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(schema = @Schema(implementation = UserStatus.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = "UserStatus with userId {userId} not found")
          )
      )
  })
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatus updatedStatus = userStatusService.updateUserStatus(userId,
        request.getNewLastActiveAt());

    UserStatusDto userStatusDto = userStatusMapper.toUserStatusDto(updatedStatus);
    return ResponseEntity.ok(userStatusDto);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
    User user = userService.findUserByUserId(userId);
    UserDto userDto = userMapper.toUserDto(user);
    return ResponseEntity.ok(userDto);
  }
}
