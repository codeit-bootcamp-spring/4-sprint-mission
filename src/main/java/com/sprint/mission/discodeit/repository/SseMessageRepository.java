package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
  private static final int MAX_EVENTS = 1000;
  private static final long MAX_AGE_HOURS = 24; // 이벤트는 24시간동안 보유

  public UUID save(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage sseMessage = new SseMessage(eventId, eventName, data, Instant.now());

    messages.put(eventId, sseMessage);
    eventIdQueue.addLast(eventId);

    // 안하면 계속 추가되기만 하고 삭제가 안됨
    if (eventIdQueue.size() > MAX_EVENTS) {
      UUID oldestId = eventIdQueue.pollFirst();
      if (oldestId != null) {
        messages.remove(oldestId);
        log.trace("Removed oldest event: {}", oldestId);
      }
    }

    log.debug("Saved SSE message: eventId={}, eventName={}", eventId, eventName);
    return eventId;
  }

  /**
   * 클라이언트에서 LastEventId를 전송해 이벤트 유실 복원
   */
  public List<SseMessage> findEventsAfter(UUID lastEventId) {
    if (lastEventId == null) {
      return List.of();
    }

    List<SseMessage> missedEvents = new ArrayList<>();
    boolean found = false;

    // 도중에 queue가 바뀌는 것을 방지하기 위해서
    List<UUID> eventIdQueueSnapShot = List.copyOf(eventIdQueue);

    for (UUID id : eventIdQueueSnapShot) {
      if (found) {
        SseMessage sseMessage = messages.get(id);
        if (sseMessage != null) {
          missedEvents.add(sseMessage);
        }
      } else if (id.equals(lastEventId)) {
        found = true;
      }
    }

    log.info("Found {} missed events after lastEventId={}", missedEvents.size(), lastEventId);
    return missedEvents;
  }

  public SseMessage findById(UUID eventId) {
    return messages.get(eventId);
  }

  public int size() {
    return eventIdQueue.size();
  }

  public void clear() {
    eventIdQueue.clear();
    messages.clear();
    log.warn("Cleared all SSE messages from repository");
  }

  // Time-based cleanup - run every hour
  @Scheduled(fixedRate = 3600000) // 1 hour
  public void cleanupOldMessages() {
    Instant cutoff = Instant.now().minusSeconds(MAX_AGE_HOURS * 3600);
    int removed = 0;

    // 앞에서부터 (가장 오래된) 정리
    while (!eventIdQueue.isEmpty()) {
      UUID oldestId = eventIdQueue.peekFirst();
      if (oldestId == null) {
        break;
      }

      SseMessage message = messages.get(oldestId);
      if (message == null || message.createdAt().isBefore(cutoff)) {
        eventIdQueue.pollFirst();
        messages.remove(oldestId);
        removed++;
      } else {
        break; // Queue is ordered, so we can stop
      }
    }

    if (removed > 0) {
      log.info("Cleaned up {} old SSE messages", removed);
    }
  }

}
