package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {
    // 보낸 이벤트의 순서를 기억
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    // 실제 이벤트 데이터를 저장(eventId -> message)
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    // 최대 저장 개수 (가장 오래된 이벤트는 자동 제거)
    private static final int MAX_SIZE = 1000;

    // 새로운 SSE 이벤트 저장
    public void save(UUID eventId, SseMessage message) {
        eventIdQueue.addLast(eventId);
        messages.put(eventId, message);
        //메모리 관리: 너무 오래된 이벤트는 제거함
        if (eventIdQueue.size() > MAX_SIZE) {
            UUID oldestId = eventIdQueue.pollFirst();
            if (oldestId != null) {
                messages.remove(oldestId);
            }
        }
    }
    // 특정 이벤트 ID 이후의 모든 이벤트를 조회함
    // 재연결한 클라이언트에게 유실된 이벤트만 복원할 때 사용
    public List<SseMessage> findAllAfter(UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();
        boolean found = false;
        for (UUID eventId : eventIdQueue) {
            if (found) {
                SseMessage message = messages.get(eventId);
                if (message != null) {
                    result.add(message);
                }
            }
            if (eventId.equals(lastEventId)) {
                found = true;
            }
        }
        return result;
    }

    public void cleanUp() {
        while (eventIdQueue.size() > MAX_SIZE) {
            UUID oldestId = eventIdQueue.pollFirst();
            if (oldestId != null) {
                messages.remove(oldestId);
            }
        }
    }
}
