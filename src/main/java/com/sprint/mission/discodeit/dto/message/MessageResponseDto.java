package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class MessageResponseDto {
    @Schema(description = "메시지 ID", example = "d67a8775-7a3a-49ec-b2d4-018e15db0683")
    private final UUID id;

    @Schema(description = "채널 ID", example = "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63")
    private final UUID channelId;

    @Schema(description = "작성자 ID", example = "82170d94-e9e6-45df-874a-f4fb949f0835")
    private final UUID authorId;

    @Schema(description = "메시지 내용", example = "오늘의 공지 확인해주세요.")
    private final String content;

    @Schema(description = "첨부파일 ID 리스트", example = "[\"3061bca4-b6d6-42d2-93c9-1946c00d18f6\"]")
    private final List<UUID> attachmentIds;

    @Schema(description = "생성일", example = "2025-07-08T09:55:59.735Z")
    private final Instant createdAt;

    @Schema(description = "수정일", example = "2025-07-08T09:55:59.735Z")
    private final Instant updatedAt;
}
