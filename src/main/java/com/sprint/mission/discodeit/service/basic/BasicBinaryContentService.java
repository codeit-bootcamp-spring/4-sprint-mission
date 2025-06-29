package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.DTO.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    private BinaryContentResponse toDto(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getContent()
        );
    }

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        if (request.data() == null || request.data().length == 0) {
            throw new IllegalArgumentException("data is null or empty");
        }

        BinaryContent binaryContent = new BinaryContent(request.data());
        binaryContentRepository.save(binaryContent);
        return toDto(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
        return toDto(binaryContent);
    }

    @Override
    public void delete(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
        binaryContentRepository.deleteById(id);
    }
}
