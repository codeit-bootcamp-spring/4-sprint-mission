package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.ChannelUpdateEvent;
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
public class ChannelUpdateEventListener {

    private final SseService sseService;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelUpdated(ChannelUpdateEvent event) {
        log.debug("# 채널 갱신 SSE 이벤트 broadcast 시작,  event={}", event);
        sseService.broadcast(
                "channels." + event.type(),
                event.channelDto()
        );
        log.info("# 채널 갱신 이벤트 수신 완료");
    }
}
