package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.status.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentAsyncWorker {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    public void storeAndUpdateBinaryContent(UUID binaryContentId, byte[] bytes) {
        try {
            binaryContentStorage.put(binaryContentId, bytes);
            log.info("바이너리 저장 성공: id={}", binaryContentId);
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
        } catch (Exception e) {
            log.error("바이너리 저장 실패: id={}, reason={}", binaryContentId, e.getMessage(), e);
            binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
        }
    }
}
