package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    // emitter 등록
    public void add(UUID userId, SseEmitter emitter) {
        data.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);
    }

    // 특정 사용자에 대한 모든 emitter 조회
    public List<SseEmitter> get(UUID userId) {
        List<SseEmitter> list = data.get(userId);
        return (list == null || list.isEmpty()) ? List.of() : List.copyOf(list);
    }

    // 특정 emitter 제거: 원자적으로 제거 후 비면 키 삭제
    public void remove(UUID userId, SseEmitter emitter) {
        data.computeIfPresent(userId, (k, list) -> {
            list.remove(emitter);
            return list.isEmpty() ? null : list;
        });
    }

    // 해당 사용자의 모든 emitter 제거
    public void removeAll(UUID userId) {
        data.remove(userId);
    }
}
