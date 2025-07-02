package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserUpdateDto {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private BinaryContentCreateDto newProfileImage;
}
