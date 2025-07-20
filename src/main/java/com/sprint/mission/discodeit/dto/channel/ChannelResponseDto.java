package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChannelResponseDto {
    @Schema(description = "채널 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "채널 타입", example = "PUBLIC")
    private ChannelType type;

    // PUBLIC 전용
    @Schema(description = "[PUBLIC 전용] 채널명", example = "공지 채널")
    private String name;
    @Schema(description = "[PUBLIC 전용] 채널 설명", example = "공지를 하는 채널입니다.")
    private String description;

    // PRIVATE 전용
    @Schema(description = "[PRIVATE 전용] 비공개 채널에 속한 사용자 ID 리스트",
            example = "[\"82170d94-e9e6-45df-874a-f4fb949f0835\",\"b2679878-5a10-41a3-b4b0-008ae83da8dd\"]")
    private List<UUID> participantIds;

    @Schema(description = "[PRIVATE 전용] 마지막으로 메시지를 보낸 시간", example = "2025-07-08T09:55:59.735Z")
    private Instant lastMessageSentAt;
}
