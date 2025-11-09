package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.service.basic.BasicSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseEmitterRepository emitterRepository;
    private final BasicSseService sseService;
    private static final long TIMEOUT = 60L * 60L * 1000L;

    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @RequestParam UUID clientId){
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.add(clientId, emitter);

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("connected")
                            .data(Map.of("clientId", clientId))
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}
