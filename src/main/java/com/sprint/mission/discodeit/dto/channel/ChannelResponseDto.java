package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChannelResponseDto {
    private UUID id;
    private ChannelType type;

    // PUBLIC 전용
    private String name;
    private String description;

    // PRIVATE 전용
    private UUID userId;
    private UUID otherUserId;

    private Instant lastMessageSentAt;
}
