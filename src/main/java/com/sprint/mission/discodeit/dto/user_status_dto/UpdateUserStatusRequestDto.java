package com.sprint.mission.discodeit.dto.user_status_dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateUserStatusRequestDto {

    private final UUID userId;
    private final UUID userStatusId;

    public UpdateUserStatusRequestDto(UUID userId, UUID userStatusId) {
        this.userId = userId;
        this.userStatusId = userStatusId;
    }

}
