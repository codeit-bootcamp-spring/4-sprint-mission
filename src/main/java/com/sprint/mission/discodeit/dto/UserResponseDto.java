package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private Boolean isOnline;
    private Boolean hasProfile;
    private byte[] profileImage;
}
