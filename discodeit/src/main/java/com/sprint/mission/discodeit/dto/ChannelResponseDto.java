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
    private UUID channelId;
    private ChannelType channelType;
    private String channelName;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UUID> participantUserIds;

}
