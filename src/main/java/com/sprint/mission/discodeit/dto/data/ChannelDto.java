package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        @Schema(description = "채널 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "채널 공개 여부", example = "PUBLIC")
        ChannelType type,
        @Schema(description = "채널 이름", example = "Minsu's channel")
        String name,
        @Schema(description = "채널 설명", example = "Minsu님의 채널입니다")
        String description,
        @Schema(description = "참가자 UUID 리스트", example = "[\n" +
                "    \"41afbed1-2670-4431-aafe-56d6e6df99c9\",\n" +
                "    \"a7e92c8f-62d9-4d6f-b7f3-2b8be601c4a5\",\n" +
                "    \"f2c4f2c9-8fcb-4b39-b2d7-1e339d776b49\"\n" +
                "  ]")
        List<UUID> participantIds,
        @Schema(description = "마지막 메세지 작성 시간", example = "2025-07-10T02:15:30.123Z")
        Instant lastMessageAt
) {
}
