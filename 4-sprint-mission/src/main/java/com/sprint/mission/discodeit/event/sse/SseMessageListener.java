package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.event.SseMessageEvent;
import com.sprint.mission.discodeit.event.SseNotificationEvent;
import com.sprint.mission.discodeit.service.SseService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SseMessageListener {

    private final SseService sseService;

    public SseMessageListener(SseService sseService) {
        this.sseService = sseService;
    }

    @EventListener
    public void handleMessage(SseMessageEvent event) {
        sseService.broadcast(event.name(), event.data());
    }

    @EventListener
    public void handle(SseNotificationEvent event) {
        sseService.send(event.receiverIds(), event.name(), event.data());
    }
}
