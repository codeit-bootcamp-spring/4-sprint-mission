package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BasicBinaryContentService basicBinaryContentService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    BinaryContent content = event.getBinaryContent();

    try{
      binaryContentStorage.put(event.getBinaryContent().getId(), event.getData());
      log.info("Binary content stored: {}", event.getBinaryContent().getId());

      basicBinaryContentService.updateStatus(content.getId(), BinaryContentStatus.SUCCESS);
    }catch(Exception e){
      log.error("Failed to store binary content {}", event.getBinaryContent().getId(), e);
      basicBinaryContentService.updateStatus(content.getId(), BinaryContentStatus.FAIL);
    }
  }

}
