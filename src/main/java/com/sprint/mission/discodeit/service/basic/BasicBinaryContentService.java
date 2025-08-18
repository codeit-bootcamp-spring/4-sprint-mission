package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binary_content.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    @Autowired(required = false)
    private BinaryContentStorage binaryContentStorage;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BinaryContent create(MultipartFile file) {
        log.info("파일 업로드 호출");
        log.debug("파일 업로드 file = {}", file);
        BinaryContent binaryContent = new BinaryContent(file);
        binaryContentRepository.save(binaryContent);
        if (binaryContentStorage != null) {
            try {
                binaryContentStorage.put(binaryContent.getId(), file.getBytes());
            } catch (IOException e) {
                log.error("파일 입출력 실패 msg = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            log.warn("스토리지가 존재하지 않음");
        }
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
}
