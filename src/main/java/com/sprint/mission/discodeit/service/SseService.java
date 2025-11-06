package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import io.netty.util.Timeout;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.apache.tomcat.util.net.SocketEvent.TIMEOUT;

@Service
@RequiredArgsConstructor
public class SseService {
    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    private static final long TIMEOUT = 60L * 60L * 1000L;

    // Ct가 서버에 SSE 연결을 등록
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        // Emitter 생성
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);

        // Emitter의 수명주기 콜백 등록
        sseEmitter.onCompletion(()->sseEmitterRepository.remove(receiverId, sseEmitter));
        sseEmitter.onTimeout(()->sseEmitterRepository.remove(receiverId, sseEmitter));
        sseEmitter.onError((ex) -> sseEmitterRepository.remove(receiverId, sseEmitter));

        // Emitter 등록
        sseEmitterRepository.add(receiverId, sseEmitter);

        // 유실된 이벤트 복원
        if (lastEventId != null) {
            List<SseMessage> missedMessages = sseMessageRepository.findAllAfter(lastEventId);
            for (SseMessage sseMessage : missedMessages) {
                try {
                    sseEmitter.send(SseEmitter.event()
                            .id(sseMessage.getId().toString())
                            .name(sseMessage.getEventName())
                            .data(sseMessage.getData()));
                } catch (Exception e) {
                    sseEmitterRepository.remove(receiverId, sseEmitter);
                    break;
                }
            }
        }
        return sseEmitter;
    }

    // 이미 연결된 Ct 에게 이벤트 데이터를 전송
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage sseMessage = new SseMessage(eventId, eventName, data);

        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllById(receiverId);
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(eventId.toString())
                            .name(eventName)
                            .data(data));
                } catch (Exception e) {
                    sseEmitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    // 모든 CT에게 이벤트 데이터를 전송
    public void broadcast(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage sseMessage = new SseMessage(eventId, eventName, data);

        sseMessageRepository.save(eventId, sseMessage);
        for (UUID receiverId :sseEmitterRepository.findAllIds()) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllById(receiverId);
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(eventId.toString())
                            .name(eventName)
                            .data(data));
                } catch (Exception e) {
                    sseEmitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    // Ping 테스트 후 끊긴 연결을 제거
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        for (UUID receiverId : sseEmitterRepository.findAllIds()) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllById(receiverId);
            List<SseEmitter> deadEmitters = new ArrayList<>();

            for (SseEmitter emitter : emitters) {
                boolean alive = ping(emitter);
                if (!alive) {
                    deadEmitters.add(emitter);
                }
            }
            for (SseEmitter emitter : deadEmitters) {
                sseEmitterRepository.remove(receiverId, emitter);
            }
        }
    }

    // SSE 연결이 살아 있는지 확인하는 가벼운 헬스체크
    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .data("연결이 살아있어요."));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
