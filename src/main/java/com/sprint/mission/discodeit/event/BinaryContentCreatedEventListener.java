package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.status.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

    private final BinaryContentAsyncWorker binaryContentAsyncWorker;

    @EventListener
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {
        final UUID binaryContentId = event.getBinaryContentId();
        final byte[] bytes = event.getBytes();

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    binaryContentAsyncWorker.storeAndUpdateBinaryContent(binaryContentId, bytes);
                }
            });
            log.debug("After-commit 비동기 저장 콜백 등록: id={}", binaryContentId);
        } else {
            // 트랜잭션 밖이면 바로 비동기로
            binaryContentAsyncWorker.storeAndUpdateBinaryContent(binaryContentId, bytes);
            log.debug("비동기 저장 즉시 실행(트랜잭션 없음): id={}", binaryContentId);
        }
    }
}
