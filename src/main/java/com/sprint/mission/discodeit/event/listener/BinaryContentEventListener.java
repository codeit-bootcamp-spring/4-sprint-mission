package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.status.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.message.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentEventListener {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final SseService sseService;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(BinaryContentCreatedEvent event) {
    BinaryContent binaryContent = event.getData();
    try {
      binaryContentStorage.put(
          binaryContent.getId(),
          event.getBytes()
      );
      binaryContentService.updateStatus(
          binaryContent.getId(), BinaryContentStatus.SUCCESS
      );
    } catch (RuntimeException e) {
      binaryContentService.updateStatus(
          binaryContent.getId(), BinaryContentStatus.FAIL
      );
    }
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentUpdatedEvent event) {
    BinaryContentDto updated = event.getTo(); // 최신 상태
    UUID ownerId = updated.ownerId(); // 업로더 ID (BinaryContentDto에 있다고 가정)

    log.debug("파일 상태 변경 SSE 전송: ownerId={}, contentId={}, status={}",
            ownerId, updated.id(), updated.status());

    sseService.send(
            List.of(ownerId),                // 수신자 ID 목록
            "binaryContents.updated",        // 이벤트 이름
            updated                          // 전송할 데이터
    );
  }

}
