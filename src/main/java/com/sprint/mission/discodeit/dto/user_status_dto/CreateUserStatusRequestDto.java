package com.sprint.mission.discodeit.dto.user_status_dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateUserStatusRequestDto {

    private final UUID userId;

    public CreateUserStatusRequestDto(UUID userId) {
        this.userId = userId;
    }
}
