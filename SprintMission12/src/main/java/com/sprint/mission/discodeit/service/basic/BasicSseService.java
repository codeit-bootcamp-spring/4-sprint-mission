package com.sprint.mission.discodeit.service.basic;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class BasicSseService {

  private static final long TIMEOUT = 1000L * 60 * 60; // 1시간

  // 사용자별 emitter 리스트 (한 유저가 여러 브라우저 탭을 가질 수 있음)
  private final Map<UUID, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
    log.info("SseService initialized");
  }

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(TIMEOUT);

    emitterMap.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(emitter);

    emitter.onCompletion(() -> removeEmitter(receiverId, emitter));
    emitter.onTimeout(() -> removeEmitter(receiverId, emitter));
    emitter.onError(e -> removeEmitter(receiverId, emitter));

    // 최초 연결 시 ping 이벤트 전송
    ping(emitter);

    if (lastEventId != null) {
      log.info("{} 재연결 요청: LastEventId={}", receiverId, lastEventId);
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = emitterMap.getOrDefault(receiverId, List.of());
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(String.valueOf(Instant.now().toEpochMilli()))
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          log.warn("전송 실패: user={} ({}), emitter 제거", receiverId, e.getMessage());
          removeEmitter(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    emitterMap.forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(String.valueOf(Instant.now().toEpochMilli()))
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          log.warn("broadcast 실패: user={} ({}), emitter 제거", receiverId, e.getMessage());
          removeEmitter(receiverId, emitter);
        }
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    log.info("SSE clean-up 시작 (총 연결 수: {})", countEmitters());

    emitterMap.forEach((receiverId, emitters) -> {
      Iterator<SseEmitter> iterator = emitters.iterator();
      while (iterator.hasNext()) {
        SseEmitter emitter = iterator.next();
        if (!ping(emitter)) {
          iterator.remove();
        }
      }
      if (emitters.isEmpty()) {
        emitterMap.remove(receiverId);
      }
    });

    log.info("SSE clean-up 완료 (남은 연결 수: {})", countEmitters());
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .id(String.valueOf(Instant.now().toEpochMilli()))
          .name("ping")
          .data("ping"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void removeEmitter(UUID receiverId, SseEmitter emitter) {
    List<SseEmitter> emitters = emitterMap.get(receiverId);
    if (emitters != null) {
      emitters.remove(emitter);
      if (emitters.isEmpty()) {
        emitterMap.remove(receiverId);
      }
    }
  }

  private int countEmitters() {
    return emitterMap.values().stream().mapToInt(List::size).sum();
  }
}
