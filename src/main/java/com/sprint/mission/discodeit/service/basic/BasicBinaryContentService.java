package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binary_content.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    //    private final BinaryContentStorage binaryContentStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public BinaryContent create(MultipartFile file) {
        log.info("파일 업로드 호출");
        log.debug("파일 업로드 file = {}", file);
        BinaryContent binaryContent = new BinaryContent(file);
        binaryContentRepository.save(binaryContent);
        try {
            eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), file.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
//        if (binaryContentStorage != null) {
//            try {
//                eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), file.getBytes()));
////                binaryContentStorage.put(binaryContent.getId(), file.getBytes());
//            } catch (Exception e) {
//                log.error("파일 입출력 실패 msg = {}", e.getMessage());
//                updateStatus(binaryContent.getId(), BinaryContentStatus.FAIL);
//                throw new RuntimeException(e);
//            }
//        } else {
//            log.warn("스토리지가 존재하지 않음");
//        }
        log.info("파일 업로드 완료 id = {}", binaryContent.getId());
        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new BinaryContentNotFoundException(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BinaryContent updateStatus(UUID binaryContentId, BinaryContentStatus status) {
        BinaryContent binaryContent = find(binaryContentId);
        binaryContent.updateStatus(status);
        return binaryContentRepository.save(binaryContent);
    }
}
