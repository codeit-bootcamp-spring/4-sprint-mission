package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponseDto {

    private UUID id;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;
}
