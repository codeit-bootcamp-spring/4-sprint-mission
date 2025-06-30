package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelUpdateDto {
    /**
     *  Public Channel만 수정 가능
     */
    @NotNull
    UUID id;

    @NotNull
    private ChannelType type;

    @NotBlank
    String name;
    String description;
}
