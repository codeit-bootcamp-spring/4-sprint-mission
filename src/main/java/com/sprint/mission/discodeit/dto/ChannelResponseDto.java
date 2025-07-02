package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ChannelResponseDto {
    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private Instant lastMessageAt;
    private List<UUID> userIds;
}
