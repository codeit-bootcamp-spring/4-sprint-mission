package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ReadStatusResponseDto {
    private UUID userId;
    private UUID channelId;
    private Instant readTime;
}
