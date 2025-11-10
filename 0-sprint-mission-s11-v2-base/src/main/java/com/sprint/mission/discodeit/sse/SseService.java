package com.sprint.mission.discodeit.sse;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private final SseRepository sseRepository;

  private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

  // 30분짜리 emitter
  private static final long DEFAULT_TIMEOUT = 1000L * 60 * 30;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    log.info("🔌 [SSE CONNECT 요청] receiverId={}, lastEventId={}", receiverId, lastEventId);

    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

    // 저장
    sseRepository.addEmitter(receiverId, emitter);
    log.info("💾 [Emitter 등록 완료] receiverId={}, 현재 연결된 emitter 수={}",
        receiverId, sseRepository.getData().get(receiverId).size());

    // 끊기면 정리
    emitter.onCompletion(() -> {
      sseRepository.removeEmitter(receiverId, emitter);
      log.info("🧹 [Emitter 종료] receiverId={} (onCompletion)", receiverId);
    });
    emitter.onTimeout(() -> {
      sseRepository.removeEmitter(receiverId, emitter);
      log.warn("⏰ [Emitter 타임아웃] receiverId={} (onTimeout)", receiverId);
    });

    // 연결 직후 더미 이벤트 한 번 보내기
    try {
      String eventId = UUID.randomUUID().toString();
      emitter.send(SseEmitter.event()
          .id(eventId)
          .name("connect")
          .data("connected"));
      log.info("✅ [연결 이벤트 전송] receiverId={}, eventId={}", receiverId, eventId);
    } catch (Exception e) {
      log.error("❌ [연결 이벤트 전송 실패] receiverId={}, error={}", receiverId, e.getMessage());
      sseRepository.removeEmitter(receiverId, emitter);
    }

    return emitter;
  }
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID receiverId : receiverIds) {
      SseMessage saved = sseRepository.addNewEvent(receiverId, eventName, data);

      List<SseEmitter> emitters = sseRepository.getData().get(receiverId);
      if (emitters == null || emitters.isEmpty()) {
        log.debug("SSE 연결 없음: userId={}", receiverId);
        continue;
      }

      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(
              SseEmitter.event()
                  .id(saved.getEventId().toString())
                  .name(saved.getEventName())
                  .data(saved.getData())
          );

          log.debug("SSE 이벤트 전송 성공: userId={}, eventId={}, eventName={}",
              receiverId, saved.getEventId(), saved.getEventName());

        } catch (Exception e) {
          log.warn("SSE 전송 실패: userId={}, 이유={}", receiverId, e.getMessage());
          sseRepository.removeEmitter(receiverId, emitter);
        }
      }
    }
  }


  public void broadcast(String eventName, Object data) {
    Set<UUID> allUserIds = sseRepository.getData().keySet();
    if (allUserIds.isEmpty()) {
      log.debug("현재 활성 SSE 연결 없음 (broadcast 스킵)");
      return;
    }

    log.debug("전체 사용자에게 SSE 브로드캐스트 시작: eventName={}", eventName);
    send(allUserIds, eventName, data);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30) // 30분마다 실행
  public void cleanUp() {
    log.debug("SSE 연결 상태 점검 시작");

    for (Map.Entry<UUID, List<SseEmitter>> entry : sseRepository.getData().entrySet()) {
      UUID receiverId = entry.getKey();
      List<SseEmitter> emitters = entry.getValue();

      if (emitters == null || emitters.isEmpty()) {
        continue;
      }

      Iterator<SseEmitter> it = emitters.iterator();
      while (it.hasNext()) {
        SseEmitter emitter = it.next();
        if (!ping(emitter)) {
          it.remove(); // 리스트에서 빼기
          log.debug("끊어진 SSE 연결 제거: userId={}", receiverId);
        }
      }

      if (emitters.isEmpty()) {
        sseRepository.getData().remove(receiverId);
      }
    }

    log.debug("✅ SSE 연결 상태 점검 완료");
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(
          SseEmitter.event()
              .name("ping")        // 이벤트 이름
              .data("keep-alive")  // 더미 데이터
              .reconnectTime(10_000L)
      );
      return true; // 성공
    } catch (Exception e) {
      return false; // 끊어졌음
    }
  }
}
// 새로운 알림 이벤트를 전송하기 위한 클래스