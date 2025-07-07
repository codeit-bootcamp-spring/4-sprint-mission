package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class MessageResponseDto {
    private final UUID id;
    private final UUID channelId;
    private final UUID authorId;
    private final String content;
    private final List<UUID> attachmentIds;
    private final Instant createdAt;
    private final Instant updatedAt;
}
