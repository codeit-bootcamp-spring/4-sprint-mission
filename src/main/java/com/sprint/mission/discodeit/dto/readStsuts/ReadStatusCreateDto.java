package com.sprint.mission.discodeit.dto.readStsuts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadStatusCreateDto {
    @NotNull
    UUID userId;
    @NotNull
    UUID channelId;
}
