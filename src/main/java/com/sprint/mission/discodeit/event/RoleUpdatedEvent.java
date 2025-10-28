package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.status.Role;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class RoleUpdatedEvent {

    private final UUID userId;
    private final Role previousRole;
    private final Role newRole;
    /*변경 수행자*/
    private final UUID updatedBy;

    public RoleUpdatedEvent(UUID userId, Role previousrole, Role newrole) {
        this(userId, previousrole, newrole, null);
    }

    public RoleUpdatedEvent(UUID userId, Role previousRole, Role newRole, UUID updatedBy) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.previousRole = Objects.requireNonNull(previousRole, "previousRole must not be null");
        this.newRole = Objects.requireNonNull(newRole, "newRole must not be null");
        if (previousRole == newRole) {
            throw new IllegalArgumentException("previousRole and newRole are the same");
        }
        this.updatedBy = updatedBy;
    }
}
