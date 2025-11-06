package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

  // 현재 연결된 SSE 세션 정보를 관리하는 레지스트리
  private final EmitterRegistry registry;
  private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 50;
  private final SseMessageRepository messageRepository;

  private final ExecutorService senderPool = new ThreadPoolExecutor(
      2, 16, 60, TimeUnit.SECONDS,
      new LinkedBlockingQueue<>(10000),
      new ThreadPoolExecutor.CallerRunsPolicy()
  );

  // SseEmitter 객체를 생성
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = createEmitter(receiverId);
    if (lastEventId != null) {
      senderPool.execute(() ->
          restoreEvents(emitter, receiverId, lastEventId)
      );
    }

    ping(emitter, receiverId, "connect", lastEventId);

    return emitter;
  }

  // SseEmitter 객체를 통해 이벤트를 전송
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = messageRepository.save(eventName, data);

    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = registry.get(receiverId);

      if (emitters.isEmpty()) {
        log.debug("No active emitters for userId={}", receiverId);
        continue;
      }

      for (SseEmitter emitter : emitters) {
        senderPool.execute(() ->
            sendToClient(emitter, receiverId, eventName, data, eventId)
        );
      }

    }
  }

  // SseEmitter 객체를 통해 이벤트를 전송
  // - 연결된 모든 클라이언트에게 동일한 Notification을 전송
  public void broadcast(String eventName, Object data) {
    UUID eventId = messageRepository.save(eventName, data);
    Set<UUID> userIds = registry.getAllUserIds();

    log.debug("Broadcasting {} to {} users", eventName, userIds.size());

    for (UUID userId : userIds) {
      List<SseEmitter> emitters = registry.get(userId);
      for (SseEmitter emitter : emitters) {
        senderPool.execute(() ->
            sendToClient(emitter, userId, eventName, data, eventId)
        );
      }
    }
  }

  // 클라이언트에서 LastEventId를 전송해 이벤트 유실 복원
  public void restoreEvents(SseEmitter sseEmitter, UUID userId, UUID LastEventId) {
    List<SseMessage> missedEvents = messageRepository.findEventsAfter(LastEventId);

    if (missedEvents.isEmpty()) {
      log.debug("No missed events for userId={}", userId);
      return;
    }

    log.info("Restoring {} missed events for userId={}", missedEvents.size(), userId);

    for (SseMessage message : missedEvents) {
      senderPool.execute(() ->
          sendToClient(sseEmitter, userId, message.eventName(),
              message.data(), message.id())
      );
    }

  }

  // 주기적으로 ping을 보내서 만료된 SseEmitter 객체를 삭제함
  @Scheduled(fixedDelay = 20 * 60 * 1000) // 20 mins
  public void cleanUp() {

    Set<UUID> userIds = registry.getAllUserIds();
    log.debug("[cleaup] Running heartbeat for {} users", userIds.size());

    for (UUID userId : userIds) {
      List<SseEmitter> emitters = registry.get(userId);
      for (SseEmitter emitter : emitters) {
        // ping 이벤트를 보내고, 실패(연결 끊김)하면 콜백이 자동으로 Registry에서 제거
        senderPool.execute(() ->
            ping(emitter, userId, "heartbeat", UUID.randomUUID())
        );
      }
    }
  }

  // 최초 연결 또는 만료 여부를 확인하기 위한 용도로 더미 이벤트를 보냄
//  private boolean ping(SseEmitter sseEmitter) {
  private boolean ping(SseEmitter sseEmitter, UUID userId, String eventName, UUID lastEventId) {
    return sendToClient(sseEmitter, userId, eventName, "ping", lastEventId);
  }

  // =============== Helper methods ===============

  public boolean sendToClient(SseEmitter sseEmitter, UUID userId, String eventName, Object data,
      UUID eventId) {
    try {
      sseEmitter.send(
          SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data)
      );
      log.trace("Emitter sent to {}", userId);
      return true;
    } catch (IOException e) {
      // 연결이 끊긴 클라이언트 제거
      log.warn("Sending Emitter failed, removing userId={}", userId);
      registry.remove(userId, sseEmitter);
      return false;
    } catch (IllegalStateException e) {
      log.debug("Emitter already completed for userId={}", userId);
      registry.remove(userId, sseEmitter);
      return false;
    }
  }

  private SseEmitter createEmitter(UUID userId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    registry.add(userId, emitter);
    return emitter;
  }

  @PreDestroy
  public void shutdown() {
    log.info("Shutting down SSE sender pool...");
    senderPool.shutdown();
    try {
      if (!senderPool.awaitTermination(10, TimeUnit.SECONDS)) {
        log.warn("Force shutting down sender pool");
        senderPool.shutdownNow();
      }
    } catch (InterruptedException e) {
      senderPool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
