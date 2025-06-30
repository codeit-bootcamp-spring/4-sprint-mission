package com.sprint.mission.discodeit.dto.readStsuts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadStatusUpdateDto {
    @NotNull
    UUID id;
}
