package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        UUID id,
        String name,
        String description,
        ChannelType channelType,
        Instant lastMessageTime,
        List<UUID> participantIds) {}
