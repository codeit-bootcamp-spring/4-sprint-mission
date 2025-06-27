package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MessageResponseDto {
    private UUID messageId;
    private UUID authorId;
    private UUID channelId;
    private String content;
    private List<BinaryContentDto> attachments;
    private Instant createdAt;
    private Instant updatedAt;
}
