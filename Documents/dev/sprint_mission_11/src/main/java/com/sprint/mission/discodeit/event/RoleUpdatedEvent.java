package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record RoleUpdatedEvent (
        UUID receiverId,
        String previousRole,
        String newRole
)

{
}
