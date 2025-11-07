package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicSseService implements SseService {
    private static final long TIMEOUT = 60L * 60L * 1000L; // 1시간

    private final SseEmitterRepository emitterRepository;

    // 클라이언트 연결 처리 -> emitter 생성 & repository 저장
    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        log.debug("# SseEmitter 생성 및 저장 요청: receiverId={}, lastEventId={}", receiverId, lastEventId);

        // 만료 시간이 TIMEOUT(현재 1시간)인 SseEmitter 객체 생성
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        // emitter 등록
        emitterRepository.put(receiverId, emitter);

        // 연결이 끊어지면 리스트에서 제거
        emitter.onCompletion(() -> {
            log.debug("# emitter 연결 종료");
            emitterRepository.delete(receiverId, emitter);
        });
        emitter.onTimeout(() -> {
            log.debug("# emitter timeout");
            emitterRepository.delete(receiverId, emitter);
        });
        emitter.onError((e) -> {
            log.error("# emitter error", e);
            emitterRepository.delete(receiverId, emitter);
        });

        log.info("# SseEmitter 등록 완료, emitter: {}", emitter);
        return emitter;
    }

    // 특정 유저들에게 이벤트 전송
    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        log.debug("# SseEmitter 객체를 통해 특정 다수에게 이벤트 전송 시작, receiverIds={}, eventName={}, data={}", receiverIds, eventName, data);

        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = emitterRepository.findByUserId(receiverId);
            if (emitters != null) {
                for (SseEmitter emitter : emitters) {
                    sendToClient(receiverId, emitter, eventName, data);
                }
            }
        }

        log.info("# SseEmitter 객체를 통해 특정 다수에게 이벤트 전송 성공");
    }

    // 전체 유저에게 이벤트 전송
    @Override
    public void broadcast(String eventName, Object data) {
        log.debug("# SseEmitter 객체를 통해 모든 클라이언트에게 이벤트 전송 시작, eventName={}, data={}", eventName, data);

        Collection<List<SseEmitter>> emitters = emitterRepository.findAllEmitters();
        for (List<SseEmitter> emitter : emitters) {
            for (SseEmitter emitterItem : emitter) {
                try {
                    if (emitterItem != null) {
                        emitterItem.send(SseEmitter.event()
                                .name(eventName)
                                .data(data));
                    }
                } catch (IOException e) {
                    log.error("# SseEmitter 객체를 통해 모든 클라이언트에게 이벤트 전송 실패", e);
                    emitters.remove(emitter);
                }
            }
        }

    }

    // 주기적으로 ping을 보내서 끊긴 연결 정리
    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 30) // 30분 주기로 정리 실행
    public void cleanUp() {
        log.debug("# SseEmitter CleanUP 시작");
        Collection<List<SseEmitter>> emitters = emitterRepository.findAllEmitters();
        for (List<SseEmitter> emitterList : emitters) {
            Iterator<SseEmitter> iterator = emitterList.iterator();

            while (iterator.hasNext()) {
                SseEmitter emitter = iterator.next();

                if (!ping(emitter)) {
                    iterator.remove(); // 끊긴 emitter 제거
                    log.info("# SseEmitter CleanUP, emitter={}", emitter);
                }
            }
        }
    }

    private void sendToClient(UUID receiverId, SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data)
                    .id(UUID.randomUUID().toString())
            );
        } catch (IOException e) {
            log.error("# 클라이언트에게 emitter 정보 전달 실패", e);
            emitterRepository.delete(receiverId, emitter);
        }
    }

    // emitter가 아직 살아있는지 확인용 더미 이벤트
    private boolean ping(SseEmitter emitter) {
        log.debug("# SseEmitter ping 시작, emitter={}", emitter);
        try {
            emitter.send(SseEmitter.event()
                    .name("ping")
                    .data("keep-alive")
            );
            log.info("# SseEmitter ping 성공");
            return true;
        } catch (IOException e) {
            log.error("# SseEmitter ping error 발생", e);
            emitter.complete();
            return false;
        }
    }
}
