package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdatedEvent {
    private UUID changedUserId;
    private Role oldRole;
    private Role newRole;
}
