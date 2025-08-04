package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusResponseDto {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
}
