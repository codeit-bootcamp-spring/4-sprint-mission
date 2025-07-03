package com.sprint.mission.discodeit.dto.channel_service_dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreatePublicChannelRequestDto {

    UUID hostUserId;
    String channelName;
    String description;

    public CreatePublicChannelRequestDto(UUID hostUserId, String channelName, String description) {
        this.hostUserId = hostUserId;
        this.channelName = channelName;
        this.description = description;
    }
}
