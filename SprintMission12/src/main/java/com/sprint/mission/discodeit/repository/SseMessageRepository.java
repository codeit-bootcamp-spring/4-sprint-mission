package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.*;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  private static final int MAX_SIZE = 500; // 보관 가능한 이벤트 수 제한

  public void save(SseMessage message) {
    if (eventIdQueue.size() >= MAX_SIZE) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) messages.remove(oldest);
    }

    eventIdQueue.addLast(message.id());
    messages.put(message.id(), message);
  }

  public List<SseMessage> findAllAfter(UUID lastEventId) {
    List<SseMessage> result = new ArrayList<>();
    boolean found = lastEventId == null;

    for (UUID id : eventIdQueue) {
      if (found) result.add(messages.get(id));
      if (id.equals(lastEventId)) found = true;
    }

    return result;
  }

  public record SseMessage(UUID id, String eventName, Object data) {}
}
