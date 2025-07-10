package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserStatusDto {

  @Schema(description = "사용자 상태 조회 요청")
  public record UserStatusRequest(
      @Schema(description = "사용자 ID", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46", format = "uuid", requiredMode = RequiredMode.REQUIRED)
      UUID userId
  ) {

  }

  @Schema(description = "사용자 상태 정보 응답")
  public record UserStatusResponse(
      @Schema(description = "사용자 상태 고유 ID", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210", format = "uuid")
      UUID userStatusId,
      @Schema(description = "사용자 ID", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46", format = "uuid")
      UUID userid,
      @Schema(
          description = "The last time the user was active, in UTC",
          format = "date-time",
          example = "2025-07-08T08:30:00Z"
      )
      Instant lastActiveTime,
      @Schema(description = "사용자 상태", example = "ONLINE")
      UserStatus.UserState userState
  ) {

  }

  @Schema(description = "사용자 상태 목록 응답")
  public record UserStatusResponses(
      @Schema(description = "사용자 상태 정보 목록")
      List<UserStatusResponse> userStatusResponses
  ) {

  }

  // ============================== PATCH - 유저 상태 업데이트 ==============================
  @Schema(description = "변경할 사용자 온라인 상태 정보")
  public record UserStatusUpdateRequest(
      @Schema(description = "새로운 마지막 활동 시각", example = "2025-07-10T10:45:00Z", requiredMode = RequiredMode.REQUIRED)
      Instant newLastActiveAt
  ) {

  }

  @Schema(description = "사용자 상태 수정 응답")
  public record UserStatusUpdateResponse(
      @Schema(description = "생성 시각", example = "2025-07-10T01:30:00Z")
      Instant createdAt,

      @Schema(description = "사용자 상태 고유 ID", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210")
      UUID id,

      @Schema(description = "수정된 마지막 활동 시각", example = "2025-07-10T10:45:00Z")
      Instant lastActiveAt,

      @Schema(description = "온라인 상태 여부", example = "true")
      Boolean online,

      @Schema(description = "수정 시각", example = "2025-07-10T10:45:00Z")
      Instant updatedAt,

      @Schema(description = "사용자 ID", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46")
      UUID userId) {

  }

}
