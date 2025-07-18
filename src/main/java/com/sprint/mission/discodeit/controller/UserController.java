package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.response.ResponseDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@ResponseBody
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "전체 User 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  [
                      {
                          "id": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                          "createdAt": "2025-07-14T05:23:03.776183Z",
                          "updatedAt": null,
                          "username": "test",
                          "email": "test@codeit.com",
                          "profileId": "cd5da335-e8ea-4764-9563-1c10bd1e1938",
                          "online": false
                      },
                      {
                          "id": "d6375170-ee7e-40ca-8868-151e5855fcfb",
                          "createdAt": "2025-07-14T05:28:44.649560Z",
                          "updatedAt": null,
                          "username": "test2",
                          "email": "test2@codeit.com",
                          "profileId": null,
                          "online": true
                      }
                  ]
                  """)
          ))
  })
  @RequestMapping(path = "/api/users", method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject("""
                  {
                      "username": "test",
                      "email": "test@codeit.com",
                      "password": "testPw",
                      "profileId": "812384b6-277b-40bb-bdd0-d6c08c892790",
                      "id": "37fd9252-55c2-41b0-a70b-692c9489c389",
                      "createdAt": "2025-07-14T04:32:24.417266Z",
                      "updatedAt": null
                  }
                  """)
          )),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject("""
                  User with email {email} already exists | User with username {username} already exists
                  """)
          ))
  })
  @RequestMapping(path = "/api/users",
      method = RequestMethod.POST,
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  User with id {id} not found
                  """)
          )),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  User with username {username} already exists
                  """)
          )),
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "username": "newTest2",
                      "email": "test2@codeit.com",
                      "password": "test2Pw",
                      "profileId": null,
                      "id": "d6375170-ee7e-40ca-8868-151e5855fcfb",
                      "createdAt": "2025-07-14T05:28:44.649560Z",
                      "updatedAt": "2025-07-14T05:47:48.610677Z"
                  }
                  """)
          ))
  })
  @RequestMapping(
      path = "/api/users/{user-id}",
      method = RequestMethod.PATCH,
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<User> update(
      @PathVariable("user-id") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class)
          )),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  User with id {id} not found
                  """)
          ))
  })
  @RequestMapping(path = "/api/users/{user-id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable("user-id") UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Operation(summary = "User 온라인 상태 업데이트")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  
                  """)
          )),
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "userId": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                      "lastActiveAt": "2025-07-14T04:30:00Z",
                      "id": "0e505783-5574-4664-b97c-28e7f8a78ad2",
                      "createdAt": "2025-07-14T05:23:03.777053Z",
                      "updatedAt": "2025-07-14T06:55:13.651444Z",
                      "online": false
                  }
                  """)
          ))
  })
  @RequestMapping(path = "/api/users/{user-id}/user-status", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatus> updateUserStatusByUserId(@PathVariable("user-id") UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
