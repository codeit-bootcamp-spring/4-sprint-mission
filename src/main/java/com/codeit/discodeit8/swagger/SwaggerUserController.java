package com.codeit.discodeit8.swagger;

import com.codeit.discodeit8.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit8.dto.user_service_dto.UserDto;
import com.codeit.discodeit8.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit8.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit8.dto.user_status_dto.UserStatusUpdateRequest;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface SwaggerUserController {

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
  ResponseEntity<List<UserDto>> findAll();

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
  ResponseEntity<UserDto> createUser(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException;

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404",
          description = "User를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(),
              examples = @ExampleObject(value = "User with id {id} not found")
          )
      )
  })
  ResponseEntity<Void> deleteUser(@PathVariable UUID userId);

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
  ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profileImage)
      throws IOException;

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
  ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request);

  ResponseEntity<UserDto> getUser(@PathVariable UUID userId);
}
