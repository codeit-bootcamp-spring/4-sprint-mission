package com.sprint.mission.discodeit.dto.channel_service_dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreatePrivateChannelRequestDto {
    public UUID hostUserId;
    public String guestName;

    public CreatePrivateChannelRequestDto(UUID hostUserId, String guestName) {
        this.hostUserId = hostUserId;
        this.guestName = guestName;
    }
}
