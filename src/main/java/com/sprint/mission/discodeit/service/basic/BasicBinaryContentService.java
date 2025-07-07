package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        try {
            String fileName = request.fileName();
            byte[] bytes = request.file().getBytes();
            String contentType = request.contentType();
            BinaryContent binaryContent = new BinaryContent(
                    fileName,
                    (long) bytes.length,
                    contentType,
                    bytes
            );
            return binaryContentRepository.save(binaryContent);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read BinaryContent file", e);
        }
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        return toDto(binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found")));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }

    @Override
    public List<UUID> findAllIds() {
        return binaryContentRepository.findAllIds();
    }


    private BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getBytes()
        );
    }
}
