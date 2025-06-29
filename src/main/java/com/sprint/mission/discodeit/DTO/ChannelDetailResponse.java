package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDetailResponse(

        UUID id,
        ChannelType type,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant latestMessageAt,
        List<UUID> participantUserIds // only for PRIVATE
) {}
