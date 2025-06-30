package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank
    private String username;

    @Email(message = "Invalid email")
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private BinaryContentCreateDto binaryContent;
}
