package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Boolean hasProfile;
    private byte[] profileImage;

}
