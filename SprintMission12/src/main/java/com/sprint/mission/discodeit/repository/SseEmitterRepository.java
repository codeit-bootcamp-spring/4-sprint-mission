package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SseEmitterRepository {

  // 유저 ID별 emitter 리스트 저장
  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID receiverId, SseEmitter emitter) {
    data.compute(receiverId, (key, emitters) -> {
      if (emitters == null) {
        emitters = new ArrayList<>();
      }
      emitters.add(emitter);
      return emitters;
    });
    return emitter;
  }

  public List<SseEmitter> get(UUID receiverId) {
    return data.getOrDefault(receiverId, Collections.emptyList());
  }

  public void delete(UUID receiverId, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters != null) {
      emitters.remove(emitter);
      if (emitters.isEmpty()) {
        data.remove(receiverId);
      }
    }
  }

  public void deleteAllById(UUID receiverId) {
    data.remove(receiverId);
  }

  public int countAllEmitters() {
    return data.values().stream().mapToInt(List::size).sum();
  }
}
