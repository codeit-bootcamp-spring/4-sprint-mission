package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class ChannelNameUpdateRequestDto {
    private UUID userId;
    private String channelOldName;
    private String channelNewName;

    public ChannelNameUpdateRequestDto(UUID userId, String channelOldName, String channelNewName) {
        this.userId = userId;
        this.channelOldName = channelOldName;
        this.channelNewName = channelNewName;
    }
}
