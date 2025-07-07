package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank
    private final String username;

    @Email(message = "Invalid email")
    @NotBlank
    private final String email;

    @NotBlank
    private final String password;

    private final BinaryContentCreateDto binaryContent;
}
