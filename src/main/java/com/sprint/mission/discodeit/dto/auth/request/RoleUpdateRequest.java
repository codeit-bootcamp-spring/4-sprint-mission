package com.sprint.mission.discodeit.dto.auth.request;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    @NotNull(message = "userId는 null이면 안됨")
    private UUID userId;

    @NotNull(message = "role은 null이면 안됨")
    private Role newRole;
}
