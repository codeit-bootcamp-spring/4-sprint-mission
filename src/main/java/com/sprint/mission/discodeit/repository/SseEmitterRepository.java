package com.sprint.mission.discodeit.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Repository
public class SseEmitterRepository {
    // ConcurrentMap은 스레드 세이프한 자료구조를 사용함
    // List<SseEmitter>는 사용자당 N개의 연결을 허용할 수 있도록 함(예 : 다중탭)
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public SseEmitter add(UUID receiverId, SseEmitter sseEmitter) {
        data.compute(receiverId, (k,v) -> {
            List<SseEmitter> list = (v == null) ? new ArrayList<>() : v;
            list.add(sseEmitter);
            return list;
        });

        sseEmitter.onCompletion(() -> { // 연결 종료시 자동 제거
            log.info("SSE 연결 완료, receiverId = {}", receiverId);
            data.remove(receiverId);
        });

        sseEmitter.onTimeout(() -> { // 타임아웃 발생시 제거
           log.warn("SSE 타임아웃, receiverId = {}", receiverId);
           data.remove(receiverId);
        });

        sseEmitter.onError((ex) -> { // 네트워크 오류 발생시 제거
            log.error("SSE 오류, receiverId = {}, error = {}", receiverId, ex.toString());
            data.remove(receiverId);
        });

        return sseEmitter;
    }

    public Optional<List<SseEmitter>> get(UUID receiverId) { // 특정 clientId에 대한 세션 조회
        return Optional.ofNullable(data.get(receiverId));
    }

    // 현재 연결된 모든 클라이언트 세션을 반환함
    // 브로드캐스트나 하트비트 전송시 전체 순회를 위해 사용됨
    public Map<UUID, List<SseEmitter>> getAll() {
        return Collections.unmodifiableMap(data);
    }

    public void remove(UUID receiverId, SseEmitter sseEmitter) {
        List<SseEmitter> list = data.get(receiverId);
        if(list == null) {
            return;
        }
        list.remove(sseEmitter);

        if(list.isEmpty()) {
            data.remove(receiverId);
        }
    }

    public void removeIfClosed(UUID receiverId) {
        List<SseEmitter> list = data.get(receiverId);
        if(list == null) {
            return;
        }
        list.removeIf(e -> { // e는 sseEmitter 타입
            try {
                e.send(SseEmitter.event().comment("probe"));
                return false;
            } catch(Exception ex) {
                return true; // 닫힘
            }
        });
        if(list.isEmpty()) {
            data.remove(receiverId);
        }
    }
}
