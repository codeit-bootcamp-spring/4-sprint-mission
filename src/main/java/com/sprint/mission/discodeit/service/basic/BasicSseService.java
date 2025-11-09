package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
@Slf4j
@Service
public class BasicSseService implements SseService {
    private static final long TIMEOUT = 60L * 60L * 1000L;
    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository sseMessageRepository;
    private final NotificationRepository notificationRepository;

    public BasicSseService(SseEmitterRepository emitterRepository, SseMessageRepository sseMessageRepository, NotificationRepository notificationRepository) {
        this.emitterRepository = emitterRepository;
        this.sseMessageRepository = sseMessageRepository;
        this.notificationRepository = notificationRepository;
    }

    // 사용자별 SseEmitter 객체를 생성하고 메시지를 전송하는 컴포넌트 구현

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) { // SseEmitter 객체 생성
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.add(receiverId, emitter);

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("connected")
                            .data(Map.of("receiverId", receiverId))
            );
        } catch(Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @Override
    public void send(UUID receiverId, String eventName, Object data) { // SseEmitter 객체를 통해 이벤트 전송

    }

    @Override
    public void broadcast(String eventName, Object data) { // SseEmitter 객체를 통해 이벤트 전송
        SseEmitter.SseEventBuilder ev = SseEmitter.event()
                .id(UUID.randomUUID().toString()) // 이벤트의 고유 ID
                .name(eventName)
                .data(data); // 전송할 데이터
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() { // 주기적으로 ping을 보내서 만료된 SseEmitter 객체를 삭제함
        for(UUID receiverId : emitterRepository.getAll().keySet()) {
            emitterRepository.removeIfClosed(receiverId);
        }
    }

    private boolean ping(SseEmitter sseEmitter) { // 최초 연결 또는 만료 여부를 확인하기 위한 용도로, 더미 이벤트를 보냄
        try {
            sseEmitter.send(sseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name("eventPing")
                    .data(Map.of("receiverId", UUID.randomUUID().toString()))
            );
            return true;
        } catch(IOException e) {
            sseEmitter.completeWithError(e);
        }
        return false;
    }
}
