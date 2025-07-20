package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(name = "User")
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private UUID profileImageId;
    private boolean isOnline;
    private UserStatus userStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
