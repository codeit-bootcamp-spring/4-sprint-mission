package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.FileUploadStatusChangedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileUploadStatusChangedListener {
    private final SseService sseService;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(FileUploadStatusChangedEvent event) {
        log.debug("# 파일 업로드 상태 변경 이벤트(FileUploadStatusChangedEvent) 수신, event={}", event);
        BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
                .orElseThrow(() -> BinaryContentNotFoundException.withId(event.binaryContentId()));
        log.debug("# 파일 업로드 상태 변경 SSE 이벤트 broadcast 시작, eventName=binaryContents.updated, payload={}", binaryContentMapper.toDto(binaryContent));
        sseService.broadcast(
                "binaryContents.updated",
                binaryContentMapper.toDto(binaryContent)
        );
        log.info("# 파일 업로드 상태 변경 이벤트 수신 완료");
    }
}
