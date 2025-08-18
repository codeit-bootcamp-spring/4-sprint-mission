package com.sprint.mission.discodeit.dto.authservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginRequestDto {
    private String email;
    private String password;

}
