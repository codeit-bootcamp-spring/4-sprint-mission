package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.event.S3UploadEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.sse.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.util.Collections;
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
  private final SseService sseService;
  private static final String SSE_BINARY_CONTENT_UPDATED_EVENT_NAME = "binaryContents.updated";


  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional
  public void handleBinaryContentCreated(S3UploadEvent event) {
    log.info("listener thread={}", Thread.currentThread().getName());
    BinaryContent binaryContent = binaryContentRepository.findById(event.binaryContentId())
        .orElseThrow(BinaryContentNotFoundException::new);

    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      BinaryContentDto binaryContentDto = binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      sseService.send(Collections.singleton(event.receiverId()), SSE_BINARY_CONTENT_UPDATED_EVENT_NAME, binaryContentDto);
      log.debug("BinaryContent 저장 성공: id={}", event.binaryContentId());
    } catch (Exception e) {
      BinaryContentDto binaryContentDto = binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
      sseService.send(Collections.singleton(event.receiverId()), SSE_BINARY_CONTENT_UPDATED_EVENT_NAME, binaryContentDto);
      log.error("BinaryContent 저장 실패: id={}, error={}", event.binaryContentId(), e.getMessage(), e);
    }
  }
}
