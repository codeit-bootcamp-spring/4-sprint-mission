package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.constant.BinaryContentErrorCode;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public void createBinaryContent(BinaryContentCreateServiceRequest request) {
        log.info("binary content to create - fileName : {}", request.getFileName());
        BinaryContent binaryContent = request.toEntity();
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), request.getBytes());
        log.info("binary content save sucessfully - id : {}", binaryContent.getId());
    }

    @Override
    @Transactional
    public void deleteById(UUID binaryContentId) {
        binaryContentRepository.deleteById(binaryContentId);
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentResponse findById(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .map(binaryContentMapper::toResponse)
                .orElseThrow(() -> new BinaryContentException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(id -> binaryContentRepository.findById(id).orElseThrow(
                        () -> new BinaryContentException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND))
                )
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponse response) {
        log.info("binary content to download - id : {}", response.getId());
        return binaryContentStorage.download(response);
    }
}
