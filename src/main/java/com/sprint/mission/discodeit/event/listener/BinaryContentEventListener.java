package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.BinaryContentUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentUploadService uploadService;
    private final BinaryContentService binaryContentService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        UUID binaryContentId = event.binaryContentId();
        byte[] bytes = event.bytes();
        int size = bytes.length;

        log.debug("트랜잭션 커밋 후 바이너리 데이터 저장 이벤트 처리 시작: id={}, size={}",
                binaryContentId,
                size
        );
        try {
            uploadService.upload(binaryContentId, bytes);
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
            log.info("바이너리 데이터 저장 성공: id={}, size={}",
                    binaryContentId,
                    size
            );
        } catch (Exception e) {
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
            log.error("바이너리 데이터 저장 실패: id={}, size={}, error={}",
                    binaryContentId,
                    size,
                    e.getMessage(),
                    e
            );
        }
    }
}
