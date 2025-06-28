package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record ChannelPrivateCreateDto(
        ChannelType channelType,
        List<UUID> userIds,
        List<ReadStatusCreateDto> readStatusCreateDtos
) {}
