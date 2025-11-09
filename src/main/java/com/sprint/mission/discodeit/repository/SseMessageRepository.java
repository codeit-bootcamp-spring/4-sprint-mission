package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository { // 메세지 저장소, 유실된것을 복원하기
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    private static final int DEFAULT_CAPACITY = 1000;
    private final int capacity;

    public SseMessageRepository() {
        this(DEFAULT_CAPACITY);
    }
    public SseMessageRepository(int capacity) {
        this.capacity = capacity;
    }

    public void put(SseMessage message) {
        UUID messageId = message.getId();
        messages.put(messageId, message);
        eventIdQueue.add(messageId);

        while(eventIdQueue.size() > capacity) {
            UUID oldId = eventIdQueue.pollFirst();
            if(oldId != null) {
                messages.remove(oldId);
            }
        }
    }

    public Optional<SseMessage> get(SseMessage message) {
        return Optional.ofNullable(messages.get(message.getId()));
    }

    public List<SseMessage> findSince(UUID lastEventId, UUID receiverId) {
        List<SseMessage> result = new ArrayList<>();
        boolean take = (lastEventId == null);
        for(UUID eventId : eventIdQueue) {

            if(take == false) {
                if(eventId.equals(lastEventId)) {
                    take = true;
                    continue;
                }
            }
            SseMessage message = messages.get(eventId);
            if(message == null) {
                continue;
            }
            if(receiver(message, receiverId)) {
                result.add(message);
            }
            result.add(message);
        }
        return result;
    }

    // 위 findSince에서 쓰는 메서드 Receiver
    private boolean receiver(SseMessage message, UUID receiverId) {
        if(message.getRecipientId() == null || message.getRecipientId().isEmpty()) {
            return true; // broadcast
        }
        return message.getRecipientId().contains(receiverId);
    }
}
