package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

    private static final long DEFAULT_TIMEOUT_MILLIS = 30 * 60 * 1000L;

    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;

    // 현재 연결 중인 사용자 집합
    private final Set<UUID> activeReceivers = ConcurrentHashMap.newKeySet();

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MILLIS);

        // 연결 수명 콜백: 저장소에서 제거 + activeReceivers 정리
        Runnable afterClose = () -> {
            emitterRepository.remove(receiverId, emitter);
            if (emitterRepository.get(receiverId).isEmpty()) {
                activeReceivers.remove(receiverId);
                log.debug("Removed receiver from active set: {}", receiverId);
            }
        };
        emitter.onCompletion(afterClose);
        emitter.onTimeout(afterClose);
        emitter.onError(ex -> afterClose.run());

        // 저장소에 등록 + activeReceivers에 추가
        emitterRepository.add(receiverId, emitter);
        activeReceivers.add(receiverId);
        log.debug("Registered emitter for receiverId={}", receiverId);

        // 연결 확인용 ping
        ping(emitter);

        // 유실 이벤트 복원
        try {
            List<SseMessage> missed =
                    (lastEventId != null) ? messageRepository.findAfter(lastEventId) : List.of();

            for (SseMessage m : missed) {
                sendToOne(emitter, m);
            }
            if (!missed.isEmpty()) {
                log.debug("Replayed {} missed events to receiver={}", missed.size(), receiverId);
            }
        } catch (Exception e) {
            log.info("Failed to replay missed events: receiverId={}, err={}", receiverId, e.toString());
        }

        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }

        UUID id = UUID.randomUUID();
        SseMessage message = new SseMessage(id, eventName, data, Instant.now());
        messageRepository.save(message);

        // 각 수신자별 emitter 목록 조회 후 발송
        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = emitterRepository.get(receiverId);
            if (emitters.isEmpty()) {
                continue;
            }

            List<SseEmitter> toRemove = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                if (!sendToOne(emitter, message)) {
                    toRemove.add(emitter);
                }
            }

            // 끊긴 연결 제거 및 activeReceivers 정리
            if (!toRemove.isEmpty()) {
                toRemove.forEach(em -> emitterRepository.remove(receiverId, em));
                if (emitterRepository.get(receiverId).isEmpty()) {
                    activeReceivers.remove(receiverId);
                }
                log.debug("Removed {} dead emitters for receiver={}", toRemove.size(), receiverId);
            }
        }
    }

    public void broadcast(String eventName, Object data) {
        UUID id = UUID.randomUUID();
        SseMessage message = new SseMessage(id, eventName, data, Instant.now());
        messageRepository.save(message);

        // 현재 활성 사용자 스냅샷
        List<UUID> receivers = new ArrayList<>(activeReceivers);

        int totalSent = 0;
        for (UUID receiverId : receivers) {
            List<SseEmitter> emitters = emitterRepository.get(receiverId);
            if (emitters.isEmpty()) {
                activeReceivers.remove(receiverId); // 깨끗이 비어 있으면 active에서도 제거
                continue;
            }

            List<SseEmitter> toRemove = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                if (sendToOne(emitter, message)) {
                    totalSent++;
                } else {
                    toRemove.add(emitter);
                }
            }
            if (!toRemove.isEmpty()) {
                toRemove.forEach(em -> emitterRepository.remove(receiverId, em));
                if (emitterRepository.get(receiverId).isEmpty()) {
                    activeReceivers.remove(receiverId);
                }
                log.debug("Broadcast: removed {} dead emitters for receiver={}", toRemove.size(), receiverId);
            }
        }

        log.info("Broadcast '{}' delivered to {} emitter(s)", eventName, totalSent);
    }

    //주기적으로 청소(30분)
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        List<UUID> receivers = new ArrayList<>(activeReceivers);

        for (UUID receiverId : receivers) {
            List<SseEmitter> emitters = emitterRepository.get(receiverId);
            if (emitters.isEmpty()) {
                activeReceivers.remove(receiverId);
                continue;
            }

            List<SseEmitter> toRemove = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                if (!ping(emitter)) {
                    toRemove.add(emitter);
                }
            }

            if (!toRemove.isEmpty()) {
                toRemove.forEach(em -> emitterRepository.remove(receiverId, em));
                if (emitterRepository.get(receiverId).isEmpty()) {
                    activeReceivers.remove(receiverId);
                }
                log.debug("CleanUp: removed {} dead emitters for receiver={}", toRemove.size(), receiverId);
            }
        }

        log.info("CleanUp completed. Active receivers={}", activeReceivers.size());
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .comment("keepalive")
                    .data("ok"));
            return true;
        } catch (IOException e) {
            try { sseEmitter.complete(); } catch (Exception ignore) {}
            return false;
        }
    }

    private boolean sendToOne(SseEmitter emitter, SseMessage message) {
        try {
            emitter.send(SseEmitter.event()
                    .id(message.id().toString())
                    .name(message.eventName())
                    .data(message.data()));
            return true;
        } catch (IOException e) {
            try { emitter.complete(); } catch (Exception ignore) {}
            return false;
        }
    }
}
