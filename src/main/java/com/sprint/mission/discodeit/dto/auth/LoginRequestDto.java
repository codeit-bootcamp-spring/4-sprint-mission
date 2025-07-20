package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDto {
    @Schema(description = "사용자명", example = "joy123")
    @NotBlank
    private String username;

    @Schema(description = "비밀번호", example = "12345678")
    @NotBlank
    private String password;
}
