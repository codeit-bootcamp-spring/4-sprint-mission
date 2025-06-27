package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private UUID profileId;
    private UserStatusDto userStatusDto;
}
