package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSseEventListener {
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NotificationCreatedEvent event) {
        NotificationDto dto = event.getData();
        Set<UUID> receivers = event.getReceiverIds();
        if (receivers == null || receivers.isEmpty()) {
            sseService.broadcast("notifications.created", dto);
        } else {
            sseService.send(receivers, "notifications.created", dto);
        }
        log.debug("SSE notifications.created sent: receivers={}, notificationId={}",
                receivers != null ? receivers.size() : -1, dto.id());
    }
}
