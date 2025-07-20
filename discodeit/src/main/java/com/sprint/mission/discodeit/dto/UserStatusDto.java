package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.UserStatus.UserState;

@Schema(description = "사용자 상태 DTO")
public class UserStatusDto {

    @Schema(description = "사용자 상태 조회 요청")
    public record UserStatusRequest(
            @Schema(description = "사용자 ID", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46", format = "uuid", requiredMode = RequiredMode.REQUIRED)
            UUID userId
    ) {

    }

    @Schema(description = "사용자 상태 정보 응답")
    public record UserStatusResponse(
            @Schema(description = "사용자 상태 ID", example = "f0e9d8c7-b6a5-4321-fedc-ba9876543210", format = "uuid")
            UUID userStatusId,
            @Schema(description = "사용자 ID", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46", format = "uuid")
            UUID userid,
            @Schema(
                    description = "User 마지막 활동 시간",
                    format = "date-time",
                    example = "2025-07-08T08:30:00Z"
            )
            Instant lastActiveTime,
            @Schema(description = "사용자 상태", example = "ONLINE")
            UserState userState
    ) {

    }

    @Schema(description = "사용자 상태 목록 응답")
    public record UserStatusResponses(
            @Schema(description = "사용자 상태 정보 목록")
            List<UserStatusResponse> userStatusResponses
    ) {

    }

    // 유저 상태 업데이트
    @Schema(description = "변경할 사용자 온라인 상태 정보")
    public record UserStatusUpdateRequest(
            @Schema(description = "마지막 활동 시각 업데이트", example = "2025-07-20T19:04:51.964227", requiredMode = RequiredMode.REQUIRED)
            Instant newLastActiveAt
    ) {

    }

    @Schema(description = "사용자 상태 수정 응답")
    public record UserStatusUpdateResponse(
            @Schema(description = "생성 시각", example = "2025-07-20T01:30:00Z")
            Instant createdAt,

            @Schema(description = "사용자 상태 ID", example = "85812100-0dcd-4652-8d1f-6e41954b33f2")
            UUID id,

            @Schema(description = "수정된 마지막 활동 시각", example = "2025-07-20T19:04:51.964227")
            Instant lastActiveAt,

            @Schema(description = "온라인 상태", example = "true")
            Boolean online,

            @Schema(description = "수정 시각", example = "2025-07-20T19:04:51.964227")
            Instant updatedAt,

            @Schema(description = "사용자 ID", example = "3af067fa-309b-4f84-a10d-8aefc579a191")
            UUID userId) {

    }
}
