package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.message.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelCreatedEvent event) {
        sseService.broadcast("channels.created", event.getData());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelUpdatedEvent event) {
        sseService.broadcast("channels.updated", event.getTo()); // 최신 상태
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelDeletedEvent event) {
        sseService.broadcast("channels.deleted", event.getData());
    }
}

