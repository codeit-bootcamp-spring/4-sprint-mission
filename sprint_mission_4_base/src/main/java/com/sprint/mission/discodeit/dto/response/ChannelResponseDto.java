package com.sprint.mission.discodeit.dto.response;

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
    private UUID id;
    private ChannelType type;
    private String name;
    private String description;
    private List<UUID> participantIds;
    private Instant lastMessageAt;
}
