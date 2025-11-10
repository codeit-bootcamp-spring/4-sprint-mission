package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> Messages = new ConcurrentHashMap<>();

    public ConcurrentLinkedDeque<UUID> getLostMessages(UUID lastEventId) {
        ConcurrentLinkedDeque<UUID> lostMessages = new ConcurrentLinkedDeque<>();
        boolean found = false;
        for (UUID uuid : eventIdQueue) {
            if (found) {
                lostMessages.add(uuid);
            }
            if (uuid.equals(lastEventId)) {
                found = true;
            }
        }
        return lostMessages;
    }

    public boolean hasLostMessage(UUID lastEventId) {
        return !eventIdQueue.contains(lastEventId);
    }

    public SseMessage getMessage(UUID lostEventId) {
        return Messages.get(lostEventId);
    }

    public void addMessage(UUID messageId, SseMessage message) {
        Messages.put(messageId, message);
        eventIdQueue.add(messageId);
    }
}
