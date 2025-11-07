package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.UserUpdateEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUpdateEventListener {
    private final SseService sseService;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdateEvent(UserUpdateEvent event) {
        log.debug("# 사용자 갱신 이벤트 리스너 시작, event={}", event);
        sseService.broadcast(
                "users." + event.eventType(),
                event.userDto()
        );
        log.info("# 사용자 갱신 이벤트 리스너 성공, broadcast={}", event);
    }
}
