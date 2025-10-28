package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.service.BinaryContentStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinaryContentSaveListener {
    private final BinaryContentStorageService binaryContentStorageService;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentSaved(BinaryContentCreatedEvent event) {
        log.debug("# [이벤트 리스너] 바이너리 데이터 이벤트 리스너 시작");
        binaryContentStorageService.save(event);
    }
}
