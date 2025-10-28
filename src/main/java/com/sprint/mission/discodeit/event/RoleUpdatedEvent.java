package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.time.Clock;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RoleUpdatedEvent extends ApplicationEvent {
  private final UUID userId;
  private final Role oldRole;
  private final Role newRole;

  public  RoleUpdatedEvent(Object source, UUID userId, Role oldRole, Role newRole) {
    super(source);
    this.userId = userId;
    this.oldRole = oldRole;
    this.newRole = newRole;
  }

}
