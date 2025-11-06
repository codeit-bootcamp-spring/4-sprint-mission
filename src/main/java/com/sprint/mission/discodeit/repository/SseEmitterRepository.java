package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    // 새로운 연결이 생겼을 때 그 emitter를 등록하는 메서드
    public void add(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, k -> new ArrayList<>())
                .add(emitter);
    }

    // 특정 유저의 모든 브라우저 연결을 조회하는 메서드
    public List<SseEmitter> findAllById(UUID receiverId) {
        return data.getOrDefault(receiverId, new ArrayList<>());
    }

    // 모든 유저의 모든 브라우저 연결을 조회하는 메서드
    public List<UUID> findAllIds() {
        return new ArrayList<>(data.keySet());
    }

    // 연결이 끊긴 emitter를 repository 에서 제거하는 메서드
    public void remove(UUID receiverId, SseEmitter emitter) {
        List<SseEmitter> emitters = data.get(receiverId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                data.remove(receiverId);
            }
        }
    }
}
