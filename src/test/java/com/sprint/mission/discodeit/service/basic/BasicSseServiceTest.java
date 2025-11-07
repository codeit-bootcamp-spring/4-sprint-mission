package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicSseServiceTest {

    @Mock
    private SseEmitterRepository emitterRepository;

    @InjectMocks
    private BasicSseService sseService;

    private SseEmitter emitter;

    @BeforeEach
    void setUp() {
        emitter = SseFixture.createEmitter();
    }

    @Test
    @DisplayName("connect()는 emitter를 생성하고 repository에 저장해야 한다")
    void connectShouldPutEmitterInRepository() {
        // given
        UUID receiverId = SseFixture.USER_ID;

        // when
        SseEmitter result = sseService.connect(receiverId, null);

        // then
        assertThat(result).isNotNull();
        verify(emitterRepository, times(1)).put(eq(receiverId), any(SseEmitter.class));
    }

    @Test
    @DisplayName("send()는 지정된 유저들에게 이벤트를 전송해야 한다")
    void sendShouldSendEventToSpecificUsers() throws IOException {
        // given
        UUID receiverId = SseFixture.USER_ID;
        List<SseEmitter> emitters = List.of(spy(SseFixture.createEmitter()));

        when(emitterRepository.findByUserId(receiverId)).thenReturn(emitters);

        // when
        sseService.send(List.of(receiverId), SseFixture.EVENT_NAME, SseFixture.DATA);

        // then
        verify(emitters.get(0), times(1))
                .send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("cleanUp()은 끊긴 emitter를 제거해야 한다")
    void cleanUpShouldRemoveBrokenEmitters() throws IOException {
        // given
        SseEmitter aliveEmitter = spy(SseFixture.createEmitter());
        SseEmitter brokenEmitter = spy(SseFixture.createEmitter());

        doNothing().when(aliveEmitter).send(any(SseEmitter.SseEventBuilder.class));
        doThrow(IOException.class).when(brokenEmitter).send(any(SseEmitter.SseEventBuilder.class));

        List<SseEmitter> list = new ArrayList<>(List.of(aliveEmitter, brokenEmitter));
        when(emitterRepository.findAllEmitters()).thenReturn(List.of(list));

        // when
        sseService.cleanUp();

        // then
        assertThat(list).containsExactly(aliveEmitter);
    }

    @Test
    @DisplayName("ping()은 정상 전송 시 true를 반환해야 한다")
    void pingShouldReturnTrueWhenSendSucceeds() throws IOException {
        // given
        SseEmitter mockEmitter = mock(SseEmitter.class);

        // when
        boolean result = invokePingDirectly(mockEmitter);

        // then
        assertThat(result).isTrue();
        verify(mockEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("ping()은 IOException 발생 시 false를 반환해야 한다")
    void pingShouldReturnFalseWhenSendFails() throws IOException {
        // given
        SseEmitter mockEmitter = mock(SseEmitter.class);
        doThrow(IOException.class).when(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));

        // when
        boolean result = invokePingDirectly(mockEmitter);

        // then
        assertThat(result).isFalse();
    }

    // private helper

    private boolean invokePingDirectly(SseEmitter emitter) {
        try {
            var method = BasicSseService.class.getDeclaredMethod("ping", SseEmitter.class);
            method.setAccessible(true);
            return (boolean) method.invoke(sseService, emitter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // fixture
    static class SseFixture {
        static final UUID USER_ID = UUID.randomUUID();
        static final String EVENT_NAME = "notifications.created";
        static final Object DATA = "test data";

        static SseEmitter createEmitter() {
            return new SseEmitter(60L * 1000L);
        }
    }
}