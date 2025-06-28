package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelUpdateDto(
        UUID id,
        String newChannelName,
        String newDescription
) {}
