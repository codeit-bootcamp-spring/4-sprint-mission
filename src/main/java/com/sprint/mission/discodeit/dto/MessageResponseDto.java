package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageResponseDto {
    private UUID messageId;
    private String content;
    private UUID channelId;
    private UUID userId;
    List<UUID> binaryIds;
    private Instant createdAt;
}
