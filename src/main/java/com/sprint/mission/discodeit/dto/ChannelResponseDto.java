package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChannelResponseDto {
    private UUID channelId;
    private ChannelType type;
    private String name;
    private String description;
    private Instant lastMessageTime;
    private List<UUID> participantIds;
}
