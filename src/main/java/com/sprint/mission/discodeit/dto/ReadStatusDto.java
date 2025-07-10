package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReadStatusDto {

  @Schema(description = "메시지 읽음 상태 생성 요청")
  public record ReadStatusRequest(
      @Schema(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = RequiredMode.REQUIRED)
      UUID userId,
      @Schema(description = "채널 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", requiredMode = RequiredMode.REQUIRED)
      UUID channelId,
      @Schema(description = "마지막으로 읽은 시각", example = "2025-07-10T10:41:52Z", requiredMode = RequiredMode.REQUIRED)
      Instant lastReadAt
  ) {

  }

  @Schema(description = "읽음 상태 정보 응답")
  public record ReadStatusResponse(
      @Schema(description = "읽음 상태 고유 ID", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210")
      UUID id,

      @Schema(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID userId,

      @Schema(description = "채널 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID channelId,

      @Schema(description = "마지막으로 읽은 시각", example = "2025-07-10T10:41:52Z")
      Instant lastReadAt,

      @Schema(description = "생성 시각", example = "2025-07-10T10:41:52Z")
      Instant createdAt,

      @Schema(description = "수정 시각", example = "2025-07-10T10:41:52Z")
      Instant updatedAt
  ) {

  }

  // ============================== PATCH - ReadStatus 수정 ==============================
  @Schema(description = "읽음 상태 수정 요청")
  public record ReadStatusUpdateRequest(
      @Schema(description = "새로운 마지막 읽음 시각", example = "2025-07-10T10:41:52Z", requiredMode = RequiredMode.REQUIRED)
      Instant newLastReadAt
  ) {

  }

  @Schema(description = "읽음 상태 수정 응답")
  public record ReadStatusUpdateResponse(
      @Schema(description = "읽음 상태 고유 ID", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210")
      UUID id,

      @Schema(description = "수정된 마지막 읽음 시각", example = "2025-07-10T10:41:52Z")
      Instant lastReadAt,

      @Schema(description = "채널 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID channelId,

      @Schema(description = "생성 시각", example = "2025-07-10T10:41:52Z")
      Instant createdAt,

      @Schema(description = "수정 시각", example = "2025-07-10T10:41:52Z")
      Instant updatedAt,

      @Schema(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID userId
  ) {

  }

}
