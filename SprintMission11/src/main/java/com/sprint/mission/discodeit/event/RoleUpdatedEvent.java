package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RoleUpdatedEvent extends ApplicationEvent {

  private final UUID userId;
  private final Role role;
  private final Role newRole;

  public RoleUpdatedEvent(Object source, UUID userId, Role role, Role newRole) {
    super(source);
    this.userId = userId;
    this.role = role;
    this.newRole = newRole;
  }
}
