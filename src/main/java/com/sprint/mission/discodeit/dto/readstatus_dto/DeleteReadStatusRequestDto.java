package com.sprint.mission.discodeit.dto.readstatus_dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteReadStatusRequestDto {
    private final UUID userId;
    private final UUID channelId;
    private final UUID readStatusId;

    public DeleteReadStatusRequestDto(UUID userId, UUID channelId, UUID readStatusId) {
        this.userId = userId;
        this.channelId = channelId;
        this.readStatusId = readStatusId;
    }
}
