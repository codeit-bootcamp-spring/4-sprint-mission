package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * 현재 연결되어 있는 클라이언트 연결 객체(SseEmitter) 를 메모리에 보관
 */
@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void put(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(emitter);
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

    public List<SseEmitter> findByUserId(UUID receiverId) {
        return data.get(receiverId);
    }

    public Collection<List<SseEmitter>> findAllEmitters() {
        return data.values();
    }
}
