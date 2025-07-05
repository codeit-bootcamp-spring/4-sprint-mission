package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
