package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelUpdateDto {
    /**
     *  Public Channel만 수정 가능
     */
    @NotNull
    private final UUID id;

    @NotNull
    private final ChannelType type;

    @NotBlank
    private final String name;
    private final String description;
}
