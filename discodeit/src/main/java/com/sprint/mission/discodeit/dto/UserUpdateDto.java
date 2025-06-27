package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserUpdateDto {
    private UUID userId;
    private String username;
    private String email;
    private String password;
    private BinaryContentDto newProfile;
}
