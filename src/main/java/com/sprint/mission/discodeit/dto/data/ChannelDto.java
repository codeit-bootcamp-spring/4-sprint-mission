package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto (
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserDto> participants,
        Instant lastMessageAt
)
{}
