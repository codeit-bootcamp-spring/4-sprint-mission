package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public class UserDto {

  // ============================== POST - 유저 등록 ==============================
  @Schema(description = "사용자 등록 요청")
  public record UserCreateRequest(
      @Schema(description = "사용자 이름", example = "gildong", requiredMode = RequiredMode.REQUIRED)
      String username,

      @Schema(description = "이메일 주소", example = "hong.gildong@example.com", requiredMode = RequiredMode.REQUIRED)
      String email,

      @Schema(description = "비밀번호", example = "password123!", requiredMode = RequiredMode.REQUIRED)
      String password
  ) {

  }

  @Schema(description = "사용자 정보 응답")
  public record UserResponse(
      @Schema(description = "사용자 고유 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID id,

      @Schema(description = "사용자 이름", example = "gildong")
      String username,

      @Schema(description = "이메일 주소", example = "hong.gildong@example.com")
      String email,

      @Schema(description = "프로필 사진 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID profileId,

      @Schema(description = "사용자 현재 상태 정보")
      UserStatusResponse userStatusResponse
  ) {

  }

  // telling Swagger that UserGetDto = User schema
  // used in POST response (creation)
  @Schema(name = "User", description = "생성된 사용자 정보 상세 응답")
  public record UserGetDto(
      @Schema(description = "사용자 고유 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID id,

      @Schema(description = "생성 시각", example = "2025-07-10T10:42:00Z")
      Instant createdAt,

      @Schema(description = "마지막 수정 시각", example = "2025-07-10T10:42:00Z")
      Instant updatedAt,

      @Schema(description = "사용자 이름", example = "gildong")
      String username,

      @Schema(description = "이메일 주소", example = "hong.gildong@example.com")
      String email,

      @Schema(description = "암호화된 비밀번호 (참고용)", example = "$2a$10$vI8a8..")
      String password,

      @Schema(description = "프로필 사진 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID profileId
  ) {

  }

  // ============================== GET - 전체 유저 조회 ==============================
  @Schema(description = "전체 사용자 목록의 단일 사용자 정보")
  public record AllUserGetDto(
      @Schema(description = "사용자 고유 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID id,

      @Schema(description = "생성 시각", example = "2025-07-10T10:42:00Z")
      Instant createdAt,

      @Schema(description = "마지막 수정 시각", example = "2025-07-10T10:42:00Z")
      Instant updatedAt,

      @Schema(description = "사용자 이름", example = "gildong")
      String username,

      @Schema(description = "이메일 주소", example = "hong.gildong@example.com")
      String email,

      @Schema(description = "프로필 사진 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID profileId,

      @Schema(description = "온라인 상태 여부", example = "true")
      Boolean online
  ) {

  }


  // ============================== PATCH - 유저 수정 ==============================
  @Schema(description = "수정할 사용자 정보")
  public record UserUpdateRequest(
      @Schema(description = "새로운 사용자 이름", example = "gildong_new")
      String newUsername,

      @Schema(description = "새로운 비밀번호", example = "newPassword456!")
      String newPassword,

      @Schema(description = "새로운 이메일 주소", example = "gildong.new@example.com")
      String newEmail
  ) {

  }


  @Schema(description = "사용자 정보 수정 응답")
  public record UserUpdateResponse(
      @Schema(description = "수정된 사용자 이름", example = "gildong_new")
      String username,

      @Schema(description = "수정된 이메일 주소", example = "gildong.new@example.com")
      String email,

      @Schema(description = "프로필 사진 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID profileId,

      @Schema(description = "사용자 고유 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID id,

      @Schema(description = "생성 시각", example = "2025-07-10T10:42:00Z")
      Instant createdAt,

      @Schema(description = "마지막 수정 시각", example = "2025-07-10T10:42:00Z")
      Instant updatedAt,

      @Schema(description = "암호화된 비밀번호 (참고용)", example = "$2a$10$vI8a8..")
      String password
  ) {

  }

}
