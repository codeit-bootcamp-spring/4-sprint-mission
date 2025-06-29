package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record PrivateChannelRequsest(
        ChannelType type,
        List<UUID> participantUserIds
) { }
