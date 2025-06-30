package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelCreateDto {
    @NotNull
    private ChannelType type;

    // PUBLIC 전용
    private String name;
    private String description;

    // PRIVATE 전용
    private UUID userId;
    private UUID otherUserId;
}
