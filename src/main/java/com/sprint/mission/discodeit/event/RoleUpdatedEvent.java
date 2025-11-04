package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record RoleUpdatedEvent(
    Role role,
    User user
) {

}
