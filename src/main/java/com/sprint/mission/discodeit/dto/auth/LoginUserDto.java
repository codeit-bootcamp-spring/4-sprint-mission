package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDto {
    @NotBlank
    String username;

    @NotBlank
    String password;
}
