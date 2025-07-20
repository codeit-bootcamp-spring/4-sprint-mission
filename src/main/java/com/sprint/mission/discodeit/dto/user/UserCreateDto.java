package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@Schema(name = "UserCreateDto", description = "사용자 생성 요청 DTO")
public class UserCreateDto {
    @Schema(description = "사용자명", example = "Mingguriguri")
    @NotBlank
    private String username;

    @Schema(description = "이메일", example = "abc@gmail.com")
    @Email(message = "Invalid email")
    @NotBlank
    private String email;

    @Schema(description = "비밀번호", example = "1q2w3e4r!")
    @NotBlank
    private String password;
}
