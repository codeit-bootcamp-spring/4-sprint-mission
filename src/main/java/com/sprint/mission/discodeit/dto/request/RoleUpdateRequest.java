package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(type = "object", description = "User 권한 변경 정보")
public record RoleUpdateRequest(
        @Schema(description = "권한을 변경할 유저ID", format = "uuid")
        @NotNull
        UUID userId,
        @Schema(description = "새로운 권한", allowableValues = {"ADMIN", "CHANNEL_MANAGER", "USER"}, example = "ADMIN")
        Role newRole
) {
}
