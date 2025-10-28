package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class RoleUpdatedEventListener {

  private final BasicNotificationService basicNotificationService;

  public RoleUpdatedEventListener(BasicNotificationService basicNotificationService) {
    this.basicNotificationService = basicNotificationService;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleRoleUpdatedEvent(RoleUpdatedEvent event) {
    log.info("User [{}] role changed: {} → {}",
    event.getUserId(),
    event.getOldRole(),
    event.getNewRole()
    );

    basicNotificationService.sendRoleUpdatedNotification(event.getUserId(), event.getNewRole().name());

  }

}
