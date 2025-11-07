package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.UserCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserDeletedEvent;
import com.sprint.mission.discodeit.event.message.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSseEventListener {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserCreatedEvent event) {
        sseService.broadcast("users.created", event.getData());
        log.debug("SSE users.created sent: userId={}", event.getData().id());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserUpdatedEvent event) {
        sseService.broadcast("users.updated", event.getTo());
        log.debug("SSE users.updated sent: userId={}", event.getTo().id());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserDeletedEvent event) {
        sseService.broadcast("users.deleted", event.getData());
        log.debug("SSE users.deleted sent: userId={}", event.getData().id());
    }
}

