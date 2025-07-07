package com.sprint.mission.discodeit.dto.readStsuts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class ReadStatusResponseDto {
    private final UUID id;
    private final Instant lastReadAt;
    private final UUID userId;
    private final UUID channelId;
}
