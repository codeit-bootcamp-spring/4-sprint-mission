package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID receiverId, SseEmitter emitter) {
        List<SseEmitter> emitters = new ArrayList<>();
        if (data.containsKey(receiverId)) {
            emitters = data.get(receiverId);
        }
        emitters.add(emitter);
        data.put(receiverId, emitters);
    }

    public void remove(UUID receiverId) {
        data.remove(receiverId);
    }

    public void remove(UUID receiverId, SseEmitter emitter) {
        if (!data.containsKey(receiverId)) {
            return;
        }
        List<SseEmitter> emitters = data.get(receiverId);
        emitters.remove(emitter);
        data.put(receiverId, emitters);
    }

    public List<SseEmitter> get(UUID receiverId) {
        if (!data.containsKey(receiverId)) {
            return new ArrayList<>();
        }
        List<SseEmitter> emitters = data.get(receiverId);
        return emitters;
    }

    public Collection<UUID> getReceiverIds() {
        return data.keySet();
    }
}
