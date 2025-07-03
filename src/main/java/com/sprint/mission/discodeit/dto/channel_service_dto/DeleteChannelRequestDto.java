package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class DeleteChannelRequestDto {
    public UUID userId;
    public String channelName;

    public DeleteChannelRequestDto(UUID userId, String channelName) {
        this.userId = userId;
        this.channelName = channelName;
    }
}
