package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChannelDto {

  // Used for API SPEC
  @Schema(description = "공개 채널 생성 요청")
  public record PublicChannelCreateRequest(
      @Schema(description = "채널 이름", example = "게임 채널", requiredMode = RequiredMode.REQUIRED)
      String name,
      @Schema(description = "채널 설명", example = "자유롭게 대화하는 채널입니다.", requiredMode = RequiredMode.REQUIRED)
      String description
  ) {

  }

  @Schema(description = "채널 정보 응답")
  public record ChannelResponse(
      @Schema(description = "채널 고유 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      UUID id,

      @Schema(description = "채널 이름", example = "일반")
      String name,

      @Schema(description = "채널 설명", example = "자유롭게 대화하는 채널입니다.")
      String description,

      @Schema(description = "채널 생성 시각", example = "2025-07-10T10:30:00Z")
      Instant createdAt,

      @Schema(description = "마지막 채널 정보 수정 시각", example = "2025-07-10T10:30:00Z")
      Instant updatedAt,

      @Schema(description = "채널 타입", example = "PUBLIC")
      ChannelType type

  ) {

  }

  @Schema(description = "사용자가 속한 채널 정보 응답")
  public record UserChannelResponse(
      @Schema(description = "채널 고유 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
      UUID id,

      @Schema(description = "채널 이름", example = "일반")
      String name,

      @Schema(description = "채널 설명", example = "자유롭게 대화하는 채널입니다.")
      String description,

      @Schema(description = "채널 타입", example = "PUBLIC")
      ChannelType type,

      @Schema(description = "마지막 메시지가 수신된 시각", example = "2025-07-10T10:31:56Z")
      Instant lastMessageAt,

      @Schema(description = "채널에 참여한 사용자들의 ID 목록")
      List<UUID> participantIds
  ) {

  }

  @Schema(description = "비공개 채널(DM) 생성 요청")
  public record PrivateChannelCreateRequest(
      @Schema(description = "채널에 초대할 사용자들의 ID 목록", requiredMode = RequiredMode.REQUIRED)
      List<UUID> participantIds
  ) {

  }


  @Schema(description = "공개 채널 정보 수정 요청")
  public record PublicChannelUpdateRequest(
      @Schema(description = "새로운 채널 이름", example = "공지사항")
      String newName,
      @Schema(description = "새로운 채널 설명", example = "중요한 공지를 전달하는 채널입니다.")
      String newDescription
  ) {

  }

}
