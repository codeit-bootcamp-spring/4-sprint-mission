package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserUpdateDto {
    private BinaryContentCreateDto binaryContent;

    private UUID userId;
    private String username;
    private String password;
    private String email;
    private UUID profileImageId;
}
