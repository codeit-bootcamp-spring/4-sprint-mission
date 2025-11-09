package com.sprint.mission.discodeit.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface SseService {
    SseEmitter connect(UUID receiverId, UUID lastEventId);
    void send(UUID receiverIds, String eventName, Object data);
    void broadcast(String eventName, Object data);
    void cleanUp();
}
