package com.sprint.mission.discodeit.dto.readStsuts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusCreateDto {
    @NotNull
    private final UUID userId;
    @NotNull
    private final UUID channelId;
}
