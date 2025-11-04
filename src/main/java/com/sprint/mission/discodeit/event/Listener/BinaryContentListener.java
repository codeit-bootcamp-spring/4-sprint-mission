package com.sprint.mission.discodeit.event.Listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentListener {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onApplicationEvent(BinaryContentCreatedEvent event) {

    try {
      binaryContentStorage.put(event.id(), event.bytes());
      binaryContentService.updateStatus(event.id(), BinaryContentStatus.SUCCESS);
    } catch (Exception e) {
      log.error("Binary Content 저장 실패: ", e);
      binaryContentService.updateStatus(event.id(), BinaryContentStatus.FAIL);
    }
  }
}
