package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusResponseDto {
    private UUID userId;
    private UUID channelId;
    private Instant readAt;
    private Instant createdAt;
    private Instant updatedAt;
}

