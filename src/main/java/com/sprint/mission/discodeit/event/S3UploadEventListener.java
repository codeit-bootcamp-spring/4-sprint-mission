package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadEventListener {
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentService binaryContentService;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional
  public void handleBinaryContentCreated(S3UploadEvent event) {
    log.info("listener thread={}", Thread.currentThread().getName());
    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(BinaryContentNotFoundException::new);

    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.debug("BinaryContent 저장 성공: id={}", event.binaryContentId());
    } catch (Exception e) {
      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
      log.error("BinaryContent 저장 실패: id={}, error={}", event.binaryContentId(), e.getMessage(), e);
    }
  }
}
