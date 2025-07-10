package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {

  @Schema(description = "메시지 생성 요청")
  public record MessageCreateRequest(
      @Schema(description = "메시지 내용", example = "안녕하세요!", requiredMode = RequiredMode.REQUIRED)
      String content,

      @Schema(description = "메시지를 보낼 채널의 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = RequiredMode.REQUIRED)
      UUID channelId,

      @Schema(description = "메시지 작성자의 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", requiredMode = RequiredMode.REQUIRED)
      UUID authorId
  ) {

  }

  @Schema(description = "메시지 정보 응답")
  public record MessageResponse(
      @Schema(description = "메시지 고유 ID", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210")
      UUID id,

      @Schema(description = "메시지 내용", example = "안녕하세요! 반갑습니다.")
      String content,

      @Schema(description = "메시지가 속한 채널의 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID channelId,

      @Schema(description = "메시지 작성자의 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID authorId,

      @Schema(description = "메시지에 첨부된 파일들의 ID 목록")
      List<UUID> attachmentIds,

      @Schema(description = "메시지 생성 시각", example = "2025-07-10T10:40:00Z")
      Instant createdAt,

      @Schema(description = "마지막 메시지 수정 시각", example = "2025-07-10T10:40:00Z")
      Instant updatedAt
  ) {

  }

  @Schema(description = "메시지 목록 응답")
  public record MessageResponseDtos(
      @Schema(description = "메시지 정보 목록")
      List<MessageResponse> messageResponses
  ) {

  }

  @Schema(description = "메시지 내용 수정 요청")
  public record MessageUpdateRequestDto(
      @Schema(description = "수정할 새로운 메시지 내용", example = "내용을 수정합니다.", requiredMode = RequiredMode.REQUIRED)
      String newContent
  ) {

  }

}
