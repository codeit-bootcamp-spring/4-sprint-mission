package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    // 메모리 보호: 최대 보관 개수
    private static final int MAX_MESSAGES = 1000;

    // 메시지 저장
    public void save(SseMessage message) {
        UUID eventId = message.id();

        messages.put(eventId, message);
        eventIdQueue.addLast(message.id());

        log.debug("Saved SSE message: eventId={}, eventName={}, createdAt={}",
                eventId, message.eventName(), message.createdAt());

        while (eventIdQueue.size() > MAX_MESSAGES) {
            UUID evict = eventIdQueue.pollFirst();
            if (evict != null) {
                messages.remove(evict);
                log.debug("Removed oldest SSE message due to capacity limit: {}", evict);
            }
        }
    }

    // 특정 id의 메시지 조회
    public Optional<SseMessage> findById(UUID id) {
        SseMessage msg = messages.get(id);

        if (msg != null) {
            log.debug("SSE message found by id={} (eventName={})", id, msg.eventName());
        } else {
            log.debug("SSE message not found by id={}", id);
        }

        return Optional.ofNullable(msg);
    }

    //LastEventId 이후 메시지 조회
    public List<SseMessage> findAfter(UUID lastEventId) {
        boolean collect = (lastEventId == null);
        List<UUID> ids = new ArrayList<>();

        for (UUID id : eventIdQueue) {
            if (!collect) {
                if (id.equals(lastEventId)) {
                    collect = true;
                    log.debug("Starting collection after lastEventId={}", lastEventId);
                }
                continue;
            }
            ids.add(id);
        }

        List<SseMessage> result = ids.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .toList();

        log.info("findAfter(lastEventId={}): returning {} events", lastEventId, result.size());
        return result;
    }

    //전체 메세지 조회
    public List<SseMessage> findAll() {
        List<SseMessage> all = eventIdQueue.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .toList();

        log.debug("findAll: {} messages returned", all.size());
        return all;
    }

    // 최신 이벤트 ID 조회
    public Optional<UUID> latestEventId() {
        UUID latest = eventIdQueue.peekLast();

        if (latest != null) {
            log.debug("Latest SSE eventId={}", latest);
        } else {
            log.debug("No latest eventId (repository empty)");
        }

        return Optional.ofNullable(latest);
    }

    // 현재 저장된 메시지 수
    public int size() {
        int count = messages.size();
        log.debug("Repository size={}", count);
        return count;
    }

    // 전체 비우기
    public void clear() {
        eventIdQueue.clear();
        messages.clear();
        log.info("Cleared all SSE messages from repository");
    }
}
