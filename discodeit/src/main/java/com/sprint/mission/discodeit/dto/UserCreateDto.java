package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCreateDto {
    private String username;
    private String email;
    private String password;
    private String fileType;
    private BinaryContentDto profilePicture;
}
