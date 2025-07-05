package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;

public record ChannelCreateDto(
        String name, String description, ChannelType channelType, UUID creatorId) {}
