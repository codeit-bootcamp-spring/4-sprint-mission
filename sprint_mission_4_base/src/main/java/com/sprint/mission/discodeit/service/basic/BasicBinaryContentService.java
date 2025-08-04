package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.maper.BinaryContentMapper;
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

    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) throws IOException {
        String fileName = request.fileName();
        byte[] bytes = request.bytes().getBytes();

        String contentType = request.contentType();
        BinaryContent binaryContent = new BinaryContent(
                fileName,
                (long) bytes.length,
                contentType,
                bytes
        );
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
        return binaryContentMapper.binaryContentToResponseDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(BinaryContentRequest binaryContentIds) {

        return binaryContentRepository.findAllByIdIn(binaryContentIds.ids()).stream()
                .map(binaryContentMapper::binaryContentToResponseDto)
                .toList();
    }

    @Override
    public void delete(UUID binaryContentId) {
        if (!binaryContentRepository.existsById(binaryContentId)) {
            throw new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found");
        }
        binaryContentRepository.deleteById(binaryContentId);
    }
}
