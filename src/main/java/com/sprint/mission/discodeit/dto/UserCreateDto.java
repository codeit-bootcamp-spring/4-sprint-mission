package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreateDto {

    private String username;
    private String email;
    private String password;

    // 프로필 이미지
    private BinaryContentCreateDto binaryContent;
}
