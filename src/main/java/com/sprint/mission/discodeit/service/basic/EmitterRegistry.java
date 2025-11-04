package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// 어떤 유저가 어떤 파이프(Emitter)를 들고 있는지를 기록하는 registry
// - 메모리 내 ConcurrentHashMap을 사용하여 동시 접근 환경에서도 안전하게 관리
@Slf4j
@Repository
public class EmitterRegistry {

  // data 맵 - 현재 연결된 모든 SSE 세션을 관리
  //  - UUID: userId
  //  - value:  List<SseEmitter>
  //  ConcurrentHashMap을 사용해 멀티스레드 환경에서도 안전하게 동작
  private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> data = new ConcurrentHashMap<>();

  // add() - 새 클라이언트가 연결되면 emitter를 생성하고 등록
  // - emitter 수명주기 콜백(onCompletion, onTimeout, onError)을 통해 연결이 끊기면
  //   자동으로 레지스트리에서 제거
  public void add(UUID userId, SseEmitter emitter) {
    CopyOnWriteArrayList<SseEmitter> emitters = data.computeIfAbsent(
        userId, k -> new CopyOnWriteArrayList<>()
    );
    emitters.add(emitter);
    data.put(userId, emitters);

    // 연결 종료 시 자동 제거
    emitter.onCompletion(() -> {
      log.info("SSE 연결 완료 (userId={})", userId);
      this.remove(userId, emitter);
    });

    // 타임아웃 발생 시 제거
    emitter.onTimeout(() -> {
      log.warn("SSE 타임아웃 (userId={})", userId);
      this.remove(userId, emitter);
    });

    // 네트워크 오류 발생 시 제거
    emitter.onError((ex) -> {
      log.error("SSE 오류 (userId={}, error={})", userId, ex.toString());
      this.remove(userId, emitter);
    });
  }

  public void remove(UUID userId) {
    data.remove(userId);
  }

  // userId의 지정된 emitter만 제거
  public void remove(UUID userId, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(userId);
    if (emitters != null) {
      // 해당 Emitter 객체만 리스트에서 제거
      emitters.remove(emitter);
      log.debug("Removed emitter for user: {}. Emitters remaining: {}", userId, emitters.size());

      // 리스트가 비었다면 맵에서 해당 사용자 항목을 제거
      if (emitters.isEmpty()) {
        data.remove(userId);
        log.info("No emitters left for user: {}. Removing user from repository.", userId);
      }
    }
  }

  // 특정 userId에 대한 세션을 조회
  // Optional을 반환하여 null 안전하게 처리할 수 있음
  public List<SseEmitter> get(UUID userId) {
    return data.get(userId) != null ? List.copyOf(data.get(userId)) : List.of();
  }

  public void removeAll(UUID userId) {
    List<SseEmitter> emitters = data.remove(userId);

    if (emitters != null) {
      log.info("Removing and completing all {} emitters for user: {}", emitters.size(), userId);
      // 모든 Emitter에 대해 완료(연결 종료) 신호를 보냄
      emitters.forEach(SseEmitter::complete);
    }
  }

  // 현재 연결된 모든 클라이언트 세션을 반환함
  // 브로드캐스트나 하트비트 전송 시 전체 순회를 위해 사용됨
  public Set<UUID> getAllUserIds() {
    return data.keySet();
  }

}
