package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {
    private static final long TIMEOUT = 60L * 60L * 1000L;
    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        sseEmitterRepository.add(receiverId, emitter);
        emitter.onCompletion(
                () -> sseEmitterRepository.remove(receiverId, emitter)
        );
        emitter.onTimeout(
                () -> sseEmitterRepository.remove(receiverId, emitter)
        );
        emitter.onError(
                (e) -> sseEmitterRepository.remove(receiverId, emitter)
        );
        try {
            emitter.send(SseEmitter.event().name("connected").data(Map.of("receiverId", receiverId)));
        } catch (IOException | IllegalStateException e) {
            emitter.completeWithError(e);
        }

        if (sseMessageRepository.hasLostMessage(lastEventId) && lastEventId != null) {
            ConcurrentLinkedDeque<UUID> lostEventIdQueue = sseMessageRepository.getLostMessages(lastEventId);
            for (UUID lostEventId : lostEventIdQueue) {
                SseMessage message = sseMessageRepository.getMessage(lostEventId);
                send(List.of(receiverId), message.name(), message.data());
            }
            ;
        }
        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.get(receiverId);
            for (SseEmitter emitter : emitters) {
                if (emitter != null) {
                    try {
                        UUID randomUUID = UUID.randomUUID();
                        emitter.send(SseEmitter.event()
                                .id(randomUUID.toString())
                                .name(eventName)
                                .data(data));
                        sseMessageRepository.addMessage(randomUUID, new SseMessage(eventName, data));
                    } catch (IOException | IllegalStateException e) {
                        sseEmitterRepository.remove(receiverId, emitter);
                        emitter.completeWithError(e);
                    }
                }
            }
        }
    }

    @Override
    public void broadcast(String eventName, Object data) {
        send(sseEmitterRepository.getReceiverIds(), eventName, data);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    @Override
    public void cleanUp() {
        Collection<UUID> receiverIds = sseEmitterRepository.getReceiverIds();
        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.get(receiverId);
            for (SseEmitter emitter : emitters) {
                if (!ping(emitter)) {
                    sseEmitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }
}
