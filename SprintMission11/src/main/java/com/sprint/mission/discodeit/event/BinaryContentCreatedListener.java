package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@Async
public class BinaryContentCreatedListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  public BinaryContentCreatedListener(
      BinaryContentStorage binaryContentStorage,
      BinaryContentService binaryContentService
  ) {
    this.binaryContentStorage = binaryContentStorage;
    this.binaryContentService = binaryContentService;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    log.debug("이벤트 접수 : {}", event.getBinaryContentId());

    UUID binaryContentId = binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
    if (binaryContentId != null) {
      log.info("바이너리 컨텐츠 저장 성공 : {}", event.getBinaryContentId());
      binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
    } else {
      log.info("바이너리 컨텐츠 저장 실패 : {}", (Object) null);
      binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}
