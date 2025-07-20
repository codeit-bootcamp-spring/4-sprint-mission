package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(name = "UserUpdateRequest")
public class UserUpdateRequest {
    private BinaryContentCreateDto binaryContent;

    private UUID userId;
    private String newUsername;
    private String newEmail;
    private String newPassword;
}
